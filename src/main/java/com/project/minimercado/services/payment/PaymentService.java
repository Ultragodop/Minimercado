package com.project.minimercado.services.payment;

import com.project.minimercado.config.PaymentConfig;
import com.project.minimercado.dto.payment.PaymentRequest;
import com.project.minimercado.dto.payment.PaymentResponse;
import com.project.minimercado.exception.PaymentException;
import com.project.minimercado.model.peticiones.Response;
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
            System.out.println(headers);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    paymentConfig.getBaseUrl() + "/api/payments",
                    HttpMethod.POST,
                    entity,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new PaymentException("BAD REQUEST");
            } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new PaymentException("NO TENGO PERMISO:((((");
            }
        } catch (Exception e) {
            log.error("Error al crear el pago: {}", e.getMessage(), e);
            throw new PaymentException("Error al procesar el pago", e);
        }
        return null;
    }
} 