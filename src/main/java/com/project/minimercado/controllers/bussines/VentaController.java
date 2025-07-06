package com.project.minimercado.controllers.bussines;

import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.services.bussines.VentaService;
import com.project.minimercado.services.bussines.VentaService.DetalleVentaTemp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@Slf4j
public class VentaController {
    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;

    public VentaController(VentaService ventaService, UsuarioRepository usuarioRepository) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/tarjeta")
    public ResponseEntity<Map<String, String>> realizarVentaTarjeta(
            @RequestBody VentaTarjetaRequest request) {
        try {
            if (request == null || request.getIdUsuario() == null || request.getDetallesVenta() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Datos de venta incompletos"));
            }
            Usuario usuario = usuarioRepository.findById(request.getIdUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String paymentUrl = ventaService.realizarVentaTarjeta(
                    usuario,
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
            Usuario usuario = usuarioRepository.findById(request.getIdUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            var venta = ventaService.realizarVentaEfectivo(
                    usuario,
                    request.getDetallesVenta()
            );

            return ResponseEntity.ok(Map.of(
                    "id", venta.getId(),
                    "total", venta.getTotal(),
                    "fecha", venta.getFecha().atZone(ZoneId.of("America/Montevideo")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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
        private Usuario idUsuario;
        private List<DetalleVentaTemp> detallesVenta;
    }
} 