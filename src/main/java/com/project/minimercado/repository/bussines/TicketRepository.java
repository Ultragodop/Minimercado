package com.project.minimercado.repository.bussines;

import com.project.minimercado.dto.bussines.Facturacion.TicketDTO;
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
    @Query("SELECT t.id as id, t.numeroTicket as numeroTicket, t.fecha as fecha, t.subtotal as subtotal, t.impuestos as impuestos, t.total as total, t.metodoPago as metodoPago, t.estado as estado, t.xmlContent as xmlContent, t.pdfContent as pdfContent, t.venta.id as ventaId FROM Ticket t WHERE t.numeroTicket = :numeroTicket")
    Optional<TicketDTO> findByNumeroTicketDTO(@Param("numeroTicket") String numeroTicket);
    List<Ticket> findByVentaId(Integer ventaId);
    Optional<Ticket> findByNumeroTicket(String numeroTicket);
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