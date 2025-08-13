package com.project.minimercado.dto.bussines.Ventas;


import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.Instant;
import java.util.List;

public interface VentaDTO {
        Integer getIdVenta();
        String getNombre();
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant getFecha();
        String getTipoPago();
        String getEstado();
        Integer getTotal();
        List<DetalleVentaDTO> getDetalleVenta();


}
