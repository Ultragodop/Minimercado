package com.project.minimercado.dto.bussines.Inventario;

import java.util.Date;



public interface ProductoDTO {
    Integer getId();
    String getNombre();
    String getDescripcion();
    Double getPrecioCompra();
    Double getPrecioVenta();
    Date getFechaVencimiento();
    String getCategoriaNombre(); // Nombre de la categoría
    String getProveedorNombre(); // Nombre del proveedor
    Integer getStockActual();
    Integer getStockMinimo();
    Boolean getActivo();

}