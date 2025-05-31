package com.project.minimercado.controllers.bussines;

import com.project.minimercado.services.bussines.VentaService;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.services.bussines.VentaService.DetalleVentaTemp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@Slf4j
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/tarjeta")
    public ResponseEntity<Map<String, String>> realizarVentaTarjeta(
            @RequestBody VentaTarjetaRequest request) {
        try {
            if (request == null || request.getUsuario() == null || request.getDetallesVenta() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Datos de venta incompletos"));
            }

            String paymentUrl = ventaService.realizarVentaTarjeta(
                request.getUsuario(),
                request.getDetallesVenta()
            );
            
            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en venta con tarjeta: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error al procesar venta con tarjeta", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error interno al procesar el pago"));
        }
    }

    @PostMapping("/efectivo")
    public ResponseEntity<Map<String, Object>> realizarVentaEfectivo(
            @RequestBody VentaTarjetaRequest request) {
        try {
            var venta = ventaService.realizarVentaEfectivo(
                request.getUsuario(),
                request.getDetallesVenta()
            );
            
            return ResponseEntity.ok(Map.of(
                "id", venta.getId(),
                "total", venta.getTotal(),
                "fecha", venta.getFecha()
            ));
        } catch (Exception e) {
            log.error("Error al procesar venta en efectivo", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerVenta(@PathVariable Integer id) {
        try {
            var venta = ventaService.obtenerVenta(id);
            return ResponseEntity.ok(Map.of(
                "id", venta.getId(),
                "total", venta.getTotal(),
                "fecha", venta.getFecha(),
                "estado", venta.getEstado(),
                "tipoPago", venta.getTipoPago()
            ));
        } catch (Exception e) {
            log.error("Error al obtener venta", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @Data
    public static class VentaTarjetaRequest {
        private Usuario usuario;
        private List<DetalleVentaTemp> detallesVenta;
    }
} 