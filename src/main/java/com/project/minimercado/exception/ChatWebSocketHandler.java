package com.project.minimercado.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.dto.chat.ChatMessage;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.repository.chat.ChatMessageRepository;
import com.project.minimercado.repository.chat.SalaChatRepository;
import com.project.minimercado.repository.chat.salaUsuarioRepository;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final SalaChatRepository salaChatRepo;
    private final salaUsuarioRepository salaUsuarioRepo;
    private final UsuarioRepository usuarioRepo;
    private final ChatMessageRepository chatMessageRepository;
    private final Timestamp timestamp= new Timestamp(System.currentTimeMillis());
    private final Map<String, Set<WebSocketSession>> salasSessions = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username == null) {
            logger.warn("No se encontró el username en la sesión WebSocket");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado: usuario no encontrado"));
            return;
        }
        logger.info("Conexión WebSocket establecida para usuario: {}", username);

        // Validación de usuario en BD
        Optional<Usuario> usuarioOpt = Optional.ofNullable(usuarioRepo.findByNombre(username));
        if (usuarioOpt.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Usuario no encontrado"));
            logger.warn("Usuario no encontrado: {}", username);
            return;
        }
        Usuario usuario = usuarioOpt.get();
        logger.info("Usuario encontrado: {}", usuario.getNombre());

        // Extracción y validación de sala
        String path = Objects.requireNonNull(session.getUri()).getPath();
        if (!path.startsWith("/chat/")) {
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

        // Añade la sesión a la sala (Set previene duplicados)
        Set<WebSocketSession> sesiones =
                salasSessions.computeIfAbsent(salaNombre, key -> ConcurrentHashMap.newKeySet());

        if (sesiones.add(session)) {
            logger.info("Sesión añadida a la sala {}: {}", salaNombre, session.getId());
        } else {
            logger.info("Sesión {} ya estaba registrada en la sala {}", session.getId(), salaNombre);
        }




}


    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {

        // 1) Deserializas tu objeto de mensaje
        ChatMessage chatMessage = mapper.readValue(message.getPayload(), ChatMessage.class);
        logger.info("Mensaje recibido: {}", chatMessage);







        // 2) Extraes el nombre de la sala de la URI
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);

        // 3) Serializas de nuevo a JSON el payload que vas a enviar
        String jsonMessage = mapper.writeValueAsString(chatMessage);

        // 4) Obtienes el Set de sesiones; si no existe, usas un Set vacío
        Set<WebSocketSession> sesiones =
                salasSessions.getOrDefault(sala, Collections.emptySet());

        // 5) Iteras y envías, limpiando sesiones cerradas
        Iterator<WebSocketSession> it = sesiones.iterator();
        while (it.hasNext()) {
            WebSocketSession s = it.next();
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(jsonMessage));
            } else {
                // Remueve sesiones muertas para mantener limpio el Set
                it.remove();
            }
        }
        chatMessageRepository.insert(chatMessage.getUsuario(), chatMessage.getMensaje(), timestamp );
    }


    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        logger.warn("Conexión WebSocket cerrada: {}, estado: {}", session.getId(), status);

        String path = Objects.requireNonNull(session.getUri()).getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);


        Set<WebSocketSession> sesiones = salasSessions.get(sala);
        if (sesiones != null) {
            // Removemos la sesión cerrada
            sesiones.remove(session);

            // Si ya no quedan sesiones, eliminamos la entrada en el mapa
            if (sesiones.isEmpty()) {
                salasSessions.remove(sala);
                logger.info("Sala '{}' vacía, eliminada del registro.", sala);
            }
        }
    }

}

