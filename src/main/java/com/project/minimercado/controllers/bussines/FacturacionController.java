package com.project.minimercado.controllers.bussines;

import com.project.minimercado.dto.bussines.Facturacion.TicketDTO;
import com.project.minimercado.model.bussines.EstadoTicket;
import com.project.minimercado.model.bussines.Ticket;
import com.project.minimercado.model.peticiones.Response;
import com.project.minimercado.services.bussines.FacturacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/facturacion")
@Slf4j
public class FacturacionController {
    private final FacturacionService facturacionService;

    public FacturacionController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }
    @PostMapping("/ticketTarjeta/{transactionExternalId}")
    public ResponseEntity<Response> generarTicketTarjeta(@PathVariable String transactionExternalId) {
        try {
            Response ticket = facturacionService.generarTicketTarjeta(transactionExternalId);
            if (ticket == null) {
                log.error("No se pudo generar el ticket para la venta con transactionExternalId: {}", transactionExternalId);
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            log.error("Error al generar ticket", e);
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/ticket/{ventaId}")
    public ResponseEntity<TicketDTO> generarTicket(@PathVariable Integer ventaId) {
        try {
            Ticket ticket = facturacionService.generarTicket(ventaId);
            if (ticket == null) {
                log.error("No se pudo generar el ticket para la venta con ID: {}", ventaId);
                return ResponseEntity.badRequest().build();
            }
            TicketDTO dto = new TicketDTO(ticket);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            log.error("Error al generar ticket", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/ticket/{numeroTicket}/anular")
    public ResponseEntity<Response> anularTicket(@PathVariable String numeroTicket) {
        try {
            Response ticket = facturacionService.anularTicket(numeroTicket);
            if(ticket.getStatus().equals("400")){
                return ResponseEntity.badRequest().body(ticket);
            }
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            log.error("Error al anular ticket", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> obtenerTicketsPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fechaFin) {
        try {
            List<Ticket> tickets = facturacionService.obtenerTicketsPorFecha(fechaInicio, fechaFin);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Error al obtener tickets por fecha", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tickets/estado/{estado}")
    public ResponseEntity<List<Ticket>> obtenerTicketsPorEstado(@PathVariable EstadoTicket estado) {
        try {
            List<Ticket> tickets = facturacionService.obtenerTicketsPorEstado(estado);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            log.error("Error al obtener tickets por estado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ticket/{numeroTicket}")
    public ResponseEntity<TicketDTO> obtenerTicketPorNumero(@PathVariable String numeroTicket) {
        try {
            return ResponseEntity.ok(facturacionService.obtenerTicketPorNumero(numeroTicket));

        } catch (Exception e) {
            log.error("Error al obtener ticket por número", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ticket/{numeroTicket}/xml")
    public ResponseEntity<String> obtenerXMLTicket(@PathVariable String numeroTicket) {
        try {
            return ResponseEntity.ok(facturacionService.obtenerXML(numeroTicket));

        } catch (Exception e) {
            log.error("Error al obtener XML del ticket", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ticket/{numeroTicket}/pdf")
    public ResponseEntity<byte[]> obtenerPDFTicket(@PathVariable String numeroTicket) {
        try {
            return ResponseEntity.ok(facturacionService.obtenerPDFPorNumeroTicket(numeroTicket));

        } catch (Exception e) {
            log.error("Error al obtener PDF del ticket", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 