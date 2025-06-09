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

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}


