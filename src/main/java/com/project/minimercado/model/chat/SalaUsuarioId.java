package com.project.minimercado.model.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SalaUsuarioId implements Serializable {
    @Column(name = "sala_id")
    private Long salaId;

    @Column(name = "id_usuario")
    private Long idUsuario;

    public SalaUsuarioId() {}

    public SalaUsuarioId(Long salaId, Long idUsuario) {
        this.salaId = salaId;
        this.idUsuario = idUsuario;
    }


    public Long getSalaId() {
        return salaId;
    }

    public void setSalaId(Long salaId) {
        this.salaId = salaId;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalaUsuarioId that)) return false;
        return Objects.equals(salaId, that.salaId) && Objects.equals(idUsuario, that.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salaId, idUsuario);
    }
}
