package com.project.minimercado.model.chat;

import com.project.minimercado.model.bussines.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatmessage", schema = "minimercado")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    private SalaChat sala;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;


    private String mensaje;

    private LocalDateTime timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SalaChat getSala() {
        return sala;
    }

    public void setSala(SalaChat sala) {
        this.sala = sala;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}


