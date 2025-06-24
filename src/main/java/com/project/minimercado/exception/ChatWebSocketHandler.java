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
@AllArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final SalaChatRepository salaChatRepo;
    private final salaUsuarioRepository salaUsuarioRepo;
    private final UsuarioRepository usuarioRepo;
    // Mapa de “nombreDeSala → sesiones en esa sala”
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> salasSessions = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Obtenemos el username guardado en el interceptor
        String username = (String) session.getAttributes().get("username");
        if (username == null) {
            // No autorizado, cerramos la conexión
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado: usuario no encontrado"));
            return;
        }

        // Usamos username para buscar el usuario en BD
        Optional<Usuario> usuarioOpt = Optional.ofNullable(usuarioRepo.findByNombre(username));
        if (usuarioOpt.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Usuario no encontrado"));
            return;
        }
        Usuario usuario = usuarioOpt.get();

        // El resto de tu lógica para buscar la sala y validar
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String salaNombre = path.substring(path.lastIndexOf("/") + 1);

        Optional<SalaChat> salaOpt = salaChatRepo.findByNombre(salaNombre);
        if (salaOpt.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Sala no existe"));
            return;
        }
        SalaChat sala = salaOpt.get();

        boolean autorizado = salaUsuarioRepo.existsBySalaAndUsuario(sala, usuario);
        if (!autorizado) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado para esta sala"));
            return;
        }

        // Si pasa todo, añadimos la sesión
        salasSessions.computeIfAbsent(salaNombre, s -> new CopyOnWriteArrayList<>()).add(session);
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
