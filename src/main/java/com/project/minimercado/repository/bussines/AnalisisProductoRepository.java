package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.AnalisisProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AnalisisProductoRepository extends JpaRepository<AnalisisProducto, Integer> {

    @Query("SELECT a FROM AnalisisProducto a WHERE a.fechaInicio >= :fechaInicio AND a.fechaFin <= :fechaFin ORDER BY a.unidadesVendidas DESC")
    List<AnalisisProducto> findTopVendidos(@Param("fechaInicio") Instant fechaInicio, @Param("fechaFin") Instant fechaFin);

    @Query("SELECT a FROM AnalisisProducto a WHERE a.fechaInicio >= :fechaInicio AND a.fechaFin <= :fechaFin ORDER BY a.margenGanancia DESC")
    List<AnalisisProducto> findTopRentables(@Param("fechaInicio") Instant fechaInicio, @Param("fechaFin") Instant fechaFin);

    @Query("SELECT a FROM AnalisisProducto a WHERE a.fechaInicio >= :fechaInicio AND a.fechaFin <= :fechaFin ORDER BY a.rotacion DESC")
    List<AnalisisProducto> findTopRotacion(@Param("fechaInicio") Instant fechaInicio, @Param("fechaFin") Instant fechaFin);

    @Query("SELECT a FROM AnalisisProducto a WHERE a.periodo = :periodo ORDER BY a.fechaInicio DESC")
    List<AnalisisProducto> findByPeriodo(@Param("periodo") String periodo);
} 