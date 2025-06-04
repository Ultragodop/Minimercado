package com.project.minimercado.controllers.chat;
import com.project.minimercado.model.chat.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/mensaje")
    @SendTo("/topic/mensajes")
    public void enviarMensaje(Message mensaje) {
System.out.println(mensaje);
    }
}

