package com.project.minimercado.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.dto.chat.ChatMessage;

import com.project.minimercado.model.chat.SalaChat;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.repository.chat.ChatMessageRepository;
import com.project.minimercado.repository.chat.SalaChatRepository;
import com.project.minimercado.services.chat.SalaChatService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ChatMessageRepository chatRepository;
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final SalaChatRepository salaChatRepo;

    private final UsuarioRepository usuarioRepo;
    private final ChatMessageRepository chatMessageRepository;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private final Map<String, Set<WebSocketSession>> salasSessions = new ConcurrentHashMap<>();
   private final Map<String, String> sessionIdToSala = new ConcurrentHashMap<>();


    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private SalaChatService salaChatService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username == null) {
            logger.warn("No se encontró el username en la sesión WebSocket");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No autorizado: usuario no encontrado"));
            return;
        }


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
        salasSessions.computeIfAbsent(salaNombre, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionIdToSala.put(session.getId(), salaNombre);
    }


    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        String salaDelEmisor = sessionIdToSala.get(session.getId());
        if (salaDelEmisor == null) {
            logger.warn("Sesión {} no está registrada en ninguna sala", session.getId());
            return;
        }
        ChatMessage chatMessage = mapper.readValue(message.getPayload(), ChatMessage.class);
        logger.info("Mensaje recibido: {}", chatMessage.getMensaje());




        String jsonMessage = mapper.writeValueAsString(chatMessage);
        Set<WebSocketSession> sesiones = salasSessions.getOrDefault(salaDelEmisor, Collections.emptySet());

        Iterator<WebSocketSession> it = sesiones.iterator();
        while (it.hasNext()) {
            WebSocketSession s = it.next();
            if (!s.isOpen()) {
                it.remove();
                continue;
            }

            if (!s.getId().equals(session.getId()) ) {
                s.sendMessage(new TextMessage(jsonMessage));
            }
        }
        long start= System.currentTimeMillis();
        guardarMensaje(salaDelEmisor, chatMessage.getUsuario(), chatMessage.getMensaje(), timestamp);
        long end = System.currentTimeMillis();
        logger.info("Mensaje guardado en la base de datos en {} ms", (end - start));
    }


    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        logger.warn("Conexión WebSocket cerrada: {}, estado: {}", session.getId(), status);

        String path = Objects.requireNonNull(session.getUri()).getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);


        Set<WebSocketSession> sesiones = salasSessions.get(sala);
        if (sesiones != null) {

            sesiones.remove(session);

            if (sesiones.isEmpty()) {
                salasSessions.remove(sala);
                logger.info("Sala '{}' vacía, eliminada del registro.", sala);
            }
        }
    }
    public void guardarMensaje(String salaDelEmisor, String usuario, String mensaje, Timestamp timestamp) {

        try {
            SalaChat salaChat = salaChatRepo.findByNombre(salaDelEmisor)
                    .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada: " + salaDelEmisor));


            com.project.minimercado.model.chat.ChatMessage chatMessage = new com.project.minimercado.model.chat.ChatMessage();
            chatMessage.setSala(salaChat);
            chatMessage.setUsuario(usuarioRepo.findByNombreas(usuario)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuario)));
            chatMessage.setMensaje(mensaje);
            chatMessage.setTimestamp(timestamp.toLocalDateTime());
            chatRepository.save(chatMessage);
        }catch (Exception e)
        {
            logger.error("Error al guardar el mensaje: {}", e.getMessage());
            e.printStackTrace();
        }

    }

}

