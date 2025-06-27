package com.project.minimercado.dto.chat;

import lombok.Setter;



@Setter

public class ChatMessage  {
    private String usuario;
    private String mensaje;;

    public ChatMessage() {
    }  // Para Jackson si usás JSON

    public ChatMessage(String usuario, String mensaje) {
        this.usuario = usuario;
        this.mensaje = mensaje;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getMensaje() {
        return mensaje;
    }


}
