package com.project.minimercado.controllers.bussines;

import com.project.minimercado.dto.bussines.Ventas.VentaDTO;
import com.project.minimercado.dto.payment.Cart;
import com.project.minimercado.dto.payment.PaymentRequest;
import com.project.minimercado.model.bussines.Usuario;
import com.project.minimercado.model.bussines.Venta;
import com.project.minimercado.repository.bussines.UsuarioRepository;
import com.project.minimercado.services.bussines.VentaService;
import com.project.minimercado.services.bussines.VentaService.DetalleVentaTemp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
            Usuario usuario = usuarioRepository.findById(request.idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String paymentUrl = ventaService.realizarVentaTarjeta(
                    usuario,
                    request.getDetallesVenta()
            );


            return ResponseEntity.ok(Map.of("transactionId", paymentUrl));
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
    public ResponseEntity<VentaDTO> realizarVentaEfectivo(
            @RequestBody VentaTarjetaRequest request) {
        try {
            Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            VentaDTO venta = ventaService.realizarVentaEfectivo(usuario, request.getDetallesVenta());

           return ResponseEntity.ok(venta);
        } catch (Exception e) {
            log.error("Error al procesar venta en efectivo", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

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
    @GetMapping("/todas-ventas")
    public ResponseEntity<List<VentaDTO>> obtenerTodasVentas() {
    try{
        List<VentaDTO> ventaDTOS= ventaService.obtenerVentas();
        return  ResponseEntity.ok(ventaDTOS);

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

    @Data
    public static class VentaTarjetaRequest {
        private Long idUsuario;
        private List<DetalleVentaTemp> detallesVenta;
    }
} 