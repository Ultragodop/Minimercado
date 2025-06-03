package com.project.minimercado.dto.bussines.Inventario;

import java.util.List;

// DTO para Proveedor con nombres de productos
public interface ProveedorProductosDTO  {
    Integer getId();
    String getNombre();

    List<String> getProductosNombres();

    void setNombre(String nombre);

    void setTelefono(String telefono);

    void setEmail(String email);

    void setDireccion(String direccion);

    String getTelefono();

    String getEmail();

    String getDireccion();

    String getPedidosProveedors();
}

