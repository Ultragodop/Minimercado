package com.project.minimercado.dto.bussines.Ventas;

import java.math.BigDecimal;
import java.time.Instant;


public interface VentaDetallePlanoDTO {
    Integer getIdVenta();
    String getNombre();
    Instant getFecha();
    String getTipoPago();
    String getEstado();
    Integer getTotal();
    Integer getCantidad();
    Integer getPrecioUnitario();

    Integer getIdProducto();
    String getNombreProducto();
}