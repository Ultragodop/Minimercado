package com.project.minimercado.model.chat;

import com.project.minimercado.model.bussines.Usuario;
import jakarta.persistence.*;

@Entity

@Table(name = "sala_usuarios")
public class SalaUsuario {

    @EmbeddedId
    private SalaUsuarioId id = new SalaUsuarioId();

    @ManyToOne
    @MapsId("salaId")
    @JoinColumn(name = "sala_id")
    private SalaChat sala;

    @ManyToOne
    @MapsId("usuarioId") // ✔ corregido
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // constructor, getters y setters

    public void setSala(SalaChat sala) {
        this.sala = sala;
        this.id.setSalaId(sala.getId()); // importante para sincronizar el ID embebido
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.id.setUsuarioId(usuario.getId()); // idem
    }
}
