package com.project.minimercado.model.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SalaUsuarioId implements Serializable {

    @Column(name = "sala_id")
    private Long salaId;

    @MapsId("usuarioId")  // ✔ correcto
    private Long usuarioId;

    // equals y hashCode son obligatorios
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalaUsuarioId)) return false;
        SalaUsuarioId that = (SalaUsuarioId) o;
        return Objects.equals(salaId, that.salaId) && Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salaId, usuarioId);
    }

    // Getters y setters
}
