package com.project.minimercado.services.bussines;

import com.project.minimercado.model.bussines.EstadoTicket;
import com.project.minimercado.model.bussines.Ticket;
import com.project.minimercado.model.bussines.Venta;
import com.project.minimercado.repository.bussines.TicketRepository;
import com.project.minimercado.repository.bussines.VentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FacturacionService {
    private static final BigDecimal IVA = new BigDecimal("0.22"); // 22% IVA
    private static final DateTimeFormatter TICKET_NUMBER_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final TicketRepository ticketRepository;
    private final VentaRepository ventaRepository;

    public FacturacionService(TicketRepository ticketRepository, VentaRepository ventaRepository) {
        this.ticketRepository = ticketRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public Ticket generarTicket(Integer ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Verificar si ya existe un ticket para esta venta
        if (!ticketRepository.findByVentaId(ventaId).isEmpty()) {
            throw new RuntimeException("Ya existe un ticket para esta venta");
        }

        Ticket ticket = new Ticket();
        ticket.setVenta(venta);
        ticket.setNumeroTicket(generarNumeroTicket());
        ticket.setMetodoPago(venta.getTipoPago());

        // Calcular totales
        BigDecimal subtotal = venta.getTotal().divide(BigDecimal.ONE.add(IVA), 2, RoundingMode.HALF_UP);
        BigDecimal impuestos = venta.getTotal().subtract(subtotal);

        ticket.setSubtotal(subtotal);
        ticket.setImpuestos(impuestos);
        ticket.setTotal(venta.getTotal());

        // Generar XML y PDF
        String xmlContent = generarXML(ticket);
        byte[] pdfContent = generarPDF(ticket);

        ticket.setXmlContent(xmlContent);
        ticket.setPdfContent(pdfContent);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket anularTicket(String numeroTicket) {
        Ticket ticket = ticketRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        if (ticket.getEstado() == EstadoTicket.ANULADO) {
            throw new RuntimeException("El ticket ya está anulado");
        }

        ticket.setEstado(EstadoTicket.ANULADO);
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> obtenerTicketsPorFecha(Instant fechaInicio, Instant fechaFin) {
        return ticketRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<Ticket> obtenerTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public Optional<Ticket> obtenerTicketPorNumero(String numeroTicket) {
        return ticketRepository.findByNumeroTicket(numeroTicket);
    }

    private String generarNumeroTicket() {
        return LocalDateTime.now().format(TICKET_NUMBER_FORMAT) + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generarXML(Ticket ticket) {
        // Aquí implementarías la generación del XML según el formato requerido
        // Por ahora retornamos un XML simple de ejemplo
        return String.format("""
                        <?xml version="1.0" encoding="UTF-8"?>
                        <ticket>
                            <numero>%s</numero>
                            <fecha>%s</fecha>
                            <subtotal>%s</subtotal>
                            <impuestos>%s</impuestos>
                            <total>%s</total>
                            <metodoPago>%s</metodoPago>
                        </ticket>
                        """,
                ticket.getNumeroTicket(),
                ticket.getFecha(),
                ticket.getSubtotal(),
                ticket.getImpuestos(),
                ticket.getTotal(),
                ticket.getMetodoPago()
        );
    }

    private byte[] generarPDF(Ticket ticket) {


        return new byte[0];
    }
} 