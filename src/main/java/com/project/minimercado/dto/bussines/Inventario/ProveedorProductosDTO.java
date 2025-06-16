package com.project.minimercado.dto.bussines.Inventario;

import java.util.List;

// DTO para Proveedor con nombres de productos
public interface ProveedorProductosDTO {
    Integer getId();

    String getNombre();

    void setNombre(String nombre);

    List<String> getProductosNombres();

    String getTelefono();

    void setTelefono(String telefono);

    String getEmail();

    void setEmail(String email);

    String getDireccion();

    void setDireccion(String direccion);

    String getPedidosProveedors();
}

