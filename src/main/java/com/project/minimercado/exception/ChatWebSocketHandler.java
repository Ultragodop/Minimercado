package com.project.minimercado.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.dto.chat.ChatMessage;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.repository.chat.SalaChatRepository;
import com.project.minimercado.repository.chat.salaUsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@NoArgsConstructor(force = true)
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final SalaChatRepository salaChatRepo;
    private final salaUsuarioRepository salaUsuarioRepo;
    private final UsuarioRepository usuarioRepo;
    // Mapa de “nombreDeSala → sesiones en esa sala”
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> salasSessions = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = Objects.requireNonNull(session.getUri()).getPath();            // "/chat/user_10_42"
        String salaNombre = path.substring(path.lastIndexOf("/") + 1);

        // 2) Obtener el usuario logueado
        String username = Objects.requireNonNull(session.getPrincipal()).getName();
        assert usuarioRepo != null;
        Optional<Usuario> usuarioOpt = Optional.ofNullable(usuarioRepo.findByNombre(username));
        if (usuarioOpt.isEmpty()) {
            // No existe el usuario en BD: cerramos conexión
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Usuario no encontrado: " + username));
            return;
        }
        Usuario usuario = usuarioOpt.get();

        // 3) Buscar la sala en BD
        assert salaChatRepo != null;
        Optional<SalaChat> salaOpt = salaChatRepo.findByNombre(salaNombre);
        if (salaOpt.isEmpty()) {
            // La sala no existe: cerramos conexión
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Sala no existe: " + salaNombre));
            return;
        }
        SalaChat sala = salaOpt.get();

        // 4) Verificar que exista la relación usuario↔sala
        assert salaUsuarioRepo != null;
        boolean autorizado = salaUsuarioRepo.existsBySalaAndUsuario(sala, usuario);
        if (!autorizado) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("No autorizado para esta sala"));
            return;
        }

        // 5) Está todo OK: añado sesión a la sala
        salasSessions
                .computeIfAbsent(salaNombre, s -> new CopyOnWriteArrayList<>())
                .add(session);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {

        ChatMessage chatMessage = mapper.readValue(message.getPayload(), ChatMessage.class);


        String path = Objects.requireNonNull(session.getUri()).getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);


        String jsonMessage = mapper.writeValueAsString(chatMessage);

        List<WebSocketSession> listaSala = salasSessions.getOrDefault(sala, new CopyOnWriteArrayList<>());
        for (WebSocketSession s : listaSala) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(jsonMessage));

            }
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {

        String path = Objects.requireNonNull(session.getUri()).getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);

        List<WebSocketSession> listaSala = salasSessions.get(sala);
        if (listaSala != null) {
            listaSala.remove(session);

            if (listaSala.isEmpty()) {
                salasSessions.remove(sala);
            }
        }
    }
}
