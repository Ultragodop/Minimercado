package com.project.minimercado.dto.bussines.Ventas;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class VentaDTOImpl implements VentaDTO {
    private Integer idVenta;
    private String nombre;
    private Instant fecha;
    private String tipoPago;
    private String estado;
    private List<DetalleVentaDTO> detalleVenta;
}