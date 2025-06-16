package com.project.minimercado.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrearSalaRequest {
    String nombre;
    Long creadorId;
    List<Long> usuariosAutorizadosIds;

    // Getters y setters
}

