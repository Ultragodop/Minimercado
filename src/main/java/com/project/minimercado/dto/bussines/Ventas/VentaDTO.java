package com.project.minimercado.dto.bussines.Ventas;


import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.Instant;
import java.util.List;

public class VentaDTO {
        Integer IdVenta;
        String nombre;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant fecha;
        String tipoPago;

        public String getEstado() {
                return estado;
        }

        public void setEstado(String estado) {
                this.estado = estado;
        }

        String estado;
        List<DetalleVentaDTO> detalleVenta;

        public Integer getIdVenta() {
                return IdVenta;
        }

        public void setIdVenta(Integer idVenta) {
                this.IdVenta = idVenta;
        }

        public String getNombre() {
                return nombre;
        }

        public void setNombre(String nombre) {
                this.nombre = nombre;
        }

        public Instant getFecha() {
                return fecha;
        }

        public void setFecha(Instant fecha) {
                this.fecha = fecha;
        }

        public String getTipoPago() {
                return tipoPago;
        }

        public void setTipoPago(String tipoPago) {
                this.tipoPago = tipoPago;
        }

        public List<DetalleVentaDTO> getDetalleVenta() {
                return detalleVenta;
        }

        public void setDetalleVenta(List<DetalleVentaDTO> detalleVenta) {
                this.detalleVenta = detalleVenta;
        }
}
