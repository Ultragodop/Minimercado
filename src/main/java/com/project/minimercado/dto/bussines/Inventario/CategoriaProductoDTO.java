package com.project.minimercado.dto.bussines.Inventario;

import java.util.List;

// DTO para Categoría con nombres de productos
public interface CategoriaProductoDTO {
    Integer getId();
    String getNombre();
    List<String> getProductosNombres(); // Solo nombres de productos
}


