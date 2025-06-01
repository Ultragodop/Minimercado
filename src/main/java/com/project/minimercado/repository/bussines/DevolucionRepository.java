package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.Devolucion;
import com.project.minimercado.model.bussines.EstadoDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {
    
    @Query("SELECT d FROM Devolucion d WHERE d.estado = :estado ORDER BY d.fecha DESC")
    List<Devolucion> findByEstado(@Param("estado") EstadoDevolucion estado);

    @Query("SELECT d FROM Devolucion d WHERE d.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY d.fecha DESC")
    List<Devolucion> findByFechaBetween(@Param("fechaInicio") Instant fechaInicio, @Param("fechaFin") Instant fechaFin);

    @Query("SELECT d FROM Devolucion d WHERE d.venta.id = :idVenta ORDER BY d.fecha DESC")
    List<Devolucion> findByVentaId(@Param("idVenta") Integer idVenta);

    @Query("SELECT d FROM Devolucion d WHERE d.usuario.id = :idUsuario ORDER BY d.fecha DESC")
    List<Devolucion> findByUsuarioId(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT d FROM Devolucion d WHERE d.estado = :estado AND d.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY d.fecha DESC")
    List<Devolucion> findByEstadoAndFechaBetween(
        @Param("estado") EstadoDevolucion estado,
        @Param("fechaInicio") Instant fechaInicio,
        @Param("fechaFin") Instant fechaFin
    );
} 