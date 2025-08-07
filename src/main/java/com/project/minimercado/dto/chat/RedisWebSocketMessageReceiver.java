package com.project.minimercado.dto.chat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.websocket.ChatWebSocketHandler;
import org.springframework.stereotype.Component;


@Component
public class RedisWebSocketMessageReceiver {
    private final ObjectMapper objectMapper;
    private final ChatWebSocketHandler chatWebSocketHandler;
    public RedisWebSocketMessageReceiver(ChatWebSocketHandler chatWebSocketHandler, ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }
    public void receiveMessage(String message) {

        System.out.println("Instancia " + System.getProperty("spring.application.name") +
                " recibió de Redis: " + message);


        chatWebSocketHandler.enviarmensajeentreinstancias(message);
    }
    }

