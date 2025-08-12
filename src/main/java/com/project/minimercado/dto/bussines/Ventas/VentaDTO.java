package com.project.minimercado.dto.bussines.Ventas;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;

public interface VentaDTO {
        Integer getIdVenta();
        String getNombre();
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant getFecha();
        String getTipoPago();
        String getEstado();
        BigDecimal getTotal();
        String getProductoNombre();
        Integer getCantidad();



}
