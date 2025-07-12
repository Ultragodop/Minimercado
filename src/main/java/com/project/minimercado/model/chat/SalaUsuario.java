package com.project.minimercado.model.chat;

import com.project.minimercado.model.bussines.Usuario;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
@Entity
@Table(name = "sala_usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sala_id", "id_usuario"})
})
public class SalaUsuario implements Serializable {

    @EmbeddedId
    private SalaUsuarioId id;

    @ManyToOne
    @MapsId("salaId")
    @JoinColumn(name = "sala_id")
    private SalaChat sala;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public SalaUsuario() {}

    public SalaUsuario(SalaUsuarioId id, SalaChat sala, Usuario usuario) {
        this.id = id;
        this.sala = sala;
        this.usuario = usuario;
    }

    public SalaUsuario(SalaChat nuevaSala, Usuario creador) {
        this.id = new SalaUsuarioId(nuevaSala.getId(), creador.getId());
        this.sala = nuevaSala;
        this.usuario = creador;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public SalaChat getSala() {
        return sala;
    }

    public void setSala(SalaChat sala) {
        this.sala = sala;
    }

    public SalaUsuarioId getId() {
        return id;
    }

    public void setId(SalaUsuarioId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalaUsuario that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
