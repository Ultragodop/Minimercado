package com.project.minimercado.controllers.bussines.payment;

import com.project.minimercado.dto.payment.CallbackRequest;
import com.project.minimercado.services.bussines.VentaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentCallbackController {
    private final VentaService ventaService;

    public PaymentCallbackController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> handleCallback(@RequestBody CallbackRequest callback) {
        if (callback == null || callback.getTransactionExternalId() == null || callback.getPurchaseData() == null) {
            log.error("Callback inválido recibido");
            return ResponseEntity.badRequest().build();
        }

        log.info("Recibido callback para transacción: {}", callback.getTransactionExternalId());
        try {
            Integer status = callback.getPurchaseData().getStatus();
            if (status == null) {
                log.error("Status no válido en callback para transacción: {}", callback.getTransactionExternalId());
                return ResponseEntity.badRequest().build();
            }

            ventaService.procesarCallbackPago(
                    callback.getTransactionExternalId(),
                    status
            );
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error de validación en callback: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error procesando callback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 