package com.project.minimercado.model.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String usuario;
    private String mensaje;

    public ChatMessage() {}  // Para Jackson si usás JSON

    public ChatMessage(String usuario, String mensaje) {
        this.usuario = usuario;
        this.mensaje = mensaje;
    }


}

