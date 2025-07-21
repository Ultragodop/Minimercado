package com.project.minimercado.dto.chat;

import lombok.Setter;


@Setter

public class ChatMessageDTO {
    private String usuario;
    private String mensaje;
    private String sala;
    public ChatMessageDTO() {
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

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
