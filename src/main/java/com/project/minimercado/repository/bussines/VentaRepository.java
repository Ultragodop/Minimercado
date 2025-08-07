package com.project.minimercado.repository.bussines;


import com.project.minimercado.dto.bussines.Ventas.VentaDTO;
import com.project.minimercado.model.bussines.Venta;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    Optional<Venta> findByTransactionExternalId(String transactionExternalId);
    @Query("SELECT v.id AS idVenta, u.nombre AS nombre, v.fecha AS fecha,v.tipoPago AS tipoPago,v.estado AS estado,v.total AS total FROM Venta v JOIN v.idUsuario u where v.id = :id")
    VentaDTO findVentaDTOById(@Param("id") Integer id);
    @NotNull Optional<Venta> findById(@NotNull Integer id);


}
