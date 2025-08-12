package com.project.minimercado.dto.bussines.Ventas;

import com.project.minimercado.dto.bussines.Inventario.ProductoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoDTOImpl implements ProductoDVDTO {
    private Integer idProducto;
    private String nombreProducto;


}
