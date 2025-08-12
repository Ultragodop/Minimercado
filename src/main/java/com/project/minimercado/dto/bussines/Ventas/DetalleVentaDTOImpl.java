package com.project.minimercado.dto.bussines.Ventas;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetalleVentaDTOImpl implements DetalleVentaDTO {
    private Integer cantidad;
    private Integer precioUnitario;
    private ProductoDTOImpl producto;



}
