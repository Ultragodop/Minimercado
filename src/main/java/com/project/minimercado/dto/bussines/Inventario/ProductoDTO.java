package com.project.minimercado.dto.bussines.Inventario;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


public interface ProductoDTO {
    Integer getIdProducto();

    String getNombre();
    Long getIdCategoria(); // ID de la categoría
    String getDescripcion();

    Double getPrecioCompra();

    Double getPrecioVenta();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Date getFechaVencimiento();

    String getCategoriaNombre(); // Nombre de la categoría

    String getProveedorNombre(); // Nombre del proveedor

    Integer getStockActual();

    Integer getStockMinimo();

    Boolean getActivo();


}