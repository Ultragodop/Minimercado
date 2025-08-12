package com.project.minimercado.dto.bussines.Ventas;

public class DetalleVentaDTO {
    Integer IdProducto;
    String NombreProducto;
    Integer Cantidad;
    Integer PrecioUnitario;

    public Integer getIdProducto() {
        return IdProducto;
    }

    public void setIdProducto(Integer idProducto) {
        IdProducto = idProducto;
    }

    public String getNombreProducto() {
        return NombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        NombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return Cantidad;
    }

    public void setCantidad(Integer cantidad) {
        Cantidad = cantidad;
    }

    public Integer getPrecioUnitario() {
        return PrecioUnitario;
    }

    public void setPrecioUnitario(Integer precioUnitario) {
        PrecioUnitario = precioUnitario;
    }
}
