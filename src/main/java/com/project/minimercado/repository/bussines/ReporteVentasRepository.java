package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.ReporteVentas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReporteVentasRepository extends JpaRepository<ReporteVentas, Integer> {

    @Query("SELECT r FROM ReporteVentas r WHERE r.fechaInicio >= :fechaInicio AND r.fechaFin <= :fechaFin")
    List<ReporteVentas> findByPeriodo(@Param("fechaInicio") Instant fechaInicio, @Param("fechaFin") Instant fechaFin);

    @Query("SELECT r FROM ReporteVentas r WHERE r.metodoPago = :metodoPago AND r.fechaInicio >= :fechaInicio AND r.fechaFin <= :fechaFin")
    List<ReporteVentas> findByMetodoPagoAndPeriodo(
            @Param("metodoPago") String metodoPago,
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin
    );

    @Query("SELECT r FROM ReporteVentas r WHERE r.periodo = :periodo ORDER BY r.fecha DESC")
    List<ReporteVentas> findByPeriodo(@Param("periodo") String periodo);
} 