package com.project.minimercado.repository.bussines;

import com.project.minimercado.dto.bussines.Ventas.DetalleVentaDTO;
import com.project.minimercado.model.bussines.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    @Query("Select dv.idProducto.id as IdProducto, dv.idProducto.nombre as NombreProducto, dv.cantidad as Cantidad, dv.precioUnitario as PrecioUnitario from DetalleVenta dv where dv.idVenta.id= :id")
    List<DetalleVentaDTO> findDetalleVenta(@Param("id") Integer id);
}
