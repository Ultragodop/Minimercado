package com.project.minimercado.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.model.chat.ChatMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // Mapa de “nombreDeSala → sesiones en esa sala”
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> salasSessions = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {

        String path = Objects.requireNonNull(session.getUri()).getPath();
        // 2. El nombre de sala es todo lo que viene después de "/chat/"  toma pa vos los solucione
        String sala = path.substring(path.lastIndexOf("/") + 1);


        salasSessions
                .computeIfAbsent(sala, s -> new CopyOnWriteArrayList<>())
                .add(session);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        // 1. Parsear el JSON entrante a ChatMessage
        ChatMessage chatMessage = mapper.readValue(message.getPayload(), ChatMessage.class);

        // 2. De nuevo, obtener la sala desde la URI de quien envió
        String path = session.getUri().getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);

        // 3. Serializar el objeto ChatMessage a JSON
        String jsonMessage = mapper.writeValueAsString(chatMessage);

        // 4. Enviar únicamente a las sesiones que estén en esta misma sala
        List<WebSocketSession> listaSala = salasSessions.getOrDefault(sala, new CopyOnWriteArrayList<>());
        for (WebSocketSession s : listaSala) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        // Cuando se cierra la sesión, hay que quitarla de la sala que corresponda
        String path = session.getUri().getPath();
        String sala = path.substring(path.lastIndexOf("/") + 1);

        List<WebSocketSession> listaSala = salasSessions.get(sala);
        if (listaSala != null) {
            listaSala.remove(session);
            // Si la sala queda vacía, podés opcionalmente eliminar la clave:
            if (listaSala.isEmpty()) {
                salasSessions.remove(sala);
            }
        }
    }
}
