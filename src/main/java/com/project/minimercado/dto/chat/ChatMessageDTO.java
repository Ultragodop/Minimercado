package com.project.minimercado.dto.chat;

import lombok.Setter;


@Setter

public class ChatMessageDTO {
    private String usuario;
    private String mensaje;

    public ChatMessageDTO() {
    }  // Para Jackson si usás JSON

    public ChatMessageDTO(String usuario, String mensaje) {
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
