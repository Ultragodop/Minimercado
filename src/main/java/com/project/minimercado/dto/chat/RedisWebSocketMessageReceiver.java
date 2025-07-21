package com.project.minimercado.dto.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercado.exception.ChatWebSocketHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

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
        try {

            Map<String, Object> receivedMessage = objectMapper.readValue(message, new TypeReference<Map<String, Object>>() {});


            String toUser = (String) receivedMessage.get("toUser");

            if (toUser != null) {

                chatWebSocketHandler.sendMessageToUser(toUser, message);
            } else {
                System.err.println("Mensaje de Redis para WebSocket incompleto: falta 'toUser'. Mensaje: " + message);
            }
        } catch (IOException e) {
            System.err.println("Error deserializando mensaje de Redis para WebSocket: " + e.getMessage() + ". Mensaje original: " + message);
        } catch (Exception e) {
            System.err.println("Error inesperado al procesar mensaje de Redis: " + e.getMessage() + ". Mensaje original: " + message);
        }
    }
}

