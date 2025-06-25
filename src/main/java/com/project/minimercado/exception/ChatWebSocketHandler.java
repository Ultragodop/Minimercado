package com.project.minimercado.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.dto.chat.ChatMessage;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.repository.chat.SalaChatRepository;
import com.project.minimercado.repository.chat.salaUsuarioRepository;
import com.project.minimercado.services.chat.Jwt;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
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
            logger.warn("No se encontró el username en la sesión WebSocket");
            // No autorizado, cerramos la conexión
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado: usuario no encontrado"));

            return;
        }
        logger.info("Conexión WebSocket establecida para usuario: {}", username);

        // Usamos username para buscar el usuario en BD
        Optional<Usuario> usuarioOpt = Optional.ofNullable(usuarioRepo.findByNombre(username));
        if (usuarioOpt.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Usuario no encontrado"));
            logger.warn("Usuario no encontrado: {}", username);
            return;
        }
        Usuario usuario = usuarioOpt.get();
        logger.info("Usuario encontrado: {}", usuario.getNombre());

        String path = Objects.requireNonNull(session.getUri()).getPath();
        if (path == null || !path.startsWith("/chat/")) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Ruta no válida"));
            return;
        }
        String salaNombre = path.substring(path.lastIndexOf("/") + 1);

        Optional<SalaChat> salaOpt = salaChatRepo.findByNombre(salaNombre);
        if (salaOpt.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Sala no existe"));
            return;
        }
        SalaChat sala = salaOpt.get();

        boolean autorizado = salaUsuarioRepo.existsBySalaAndUsuario(sala, usuario);
        if (!autorizado) {
            logger.warn("Usuario {} no autorizado para la sala {}", usuario.getNombre(), salaNombre);
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado para esta sala"));
            return;
        }

        // Si pasa todo, añadimos la sesión
        salasSessions.computeIfAbsent(salaNombre, s -> new CopyOnWriteArrayList<>()).add(session);
    }



    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        logger.warn("Conexión WebSocket cerrada: {}, estado: {}", session.getId(), status);
        
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
