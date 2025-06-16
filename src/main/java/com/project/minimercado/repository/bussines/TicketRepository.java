package com.project.minimercado.repository.bussines;

import com.project.minimercado.model.bussines.EstadoTicket;
import com.project.minimercado.model.bussines.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    Optional<Ticket> findByNumeroTicket(String numeroTicket);

    List<Ticket> findByVentaId(Integer ventaId);

    List<Ticket> findByEstado(EstadoTicket estado);

    @Query("SELECT t FROM Ticket t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fecha DESC")
    List<Ticket> findByFechaBetween(@Param("fechaInicio") Instant fechaInicio, @Param("fechaFin") Instant fechaFin);

    @Query("SELECT t FROM Ticket t WHERE t.estado = :estado AND t.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fecha DESC")
    List<Ticket> findByEstadoAndFechaBetween(
            @Param("estado") EstadoTicket estado,
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin
    );
} 