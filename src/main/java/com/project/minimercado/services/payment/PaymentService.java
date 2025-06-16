package com.project.minimercado.services.payment;

import com.project.minimercado.config.PaymentConfig;
import com.project.minimercado.dto.payment.PaymentRequest;
import com.project.minimercado.dto.payment.PaymentResponse;
import com.project.minimercado.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class PaymentService {
    private final RestTemplate restTemplate;
    private final PaymentConfig paymentConfig;

    public PaymentService(RestTemplate paymentRestTemplate, PaymentConfig paymentConfig) {
        this.restTemplate = paymentRestTemplate;
        this.paymentConfig = paymentConfig;
    }

    public String createPayment(PaymentRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("merchant-secret-key", paymentConfig.getMerchantSecret());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                    paymentConfig.getBaseUrl() + "/api/payments",
                    HttpMethod.POST,
                    entity,
                    PaymentResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getUrl();
            } else {
                throw new PaymentException("Error en la respuesta del servicio de pagos");
            }
        } catch (Exception e) {
            log.error("Error al crear el pago: {}", e.getMessage(), e);
            throw new PaymentException("Error al procesar el pago", e);
        }
    }
} 