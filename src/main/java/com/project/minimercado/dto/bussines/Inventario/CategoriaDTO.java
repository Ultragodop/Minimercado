package com.project.minimercado.dto.bussines.Inventario;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Date;


// DTO para Categoría con nombres de productos
public interface CategoriaDTO {
    Integer getId();

    String getNombre();
    String getActivo();
    String getDescripcion();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant getFechaCreacion();
}


