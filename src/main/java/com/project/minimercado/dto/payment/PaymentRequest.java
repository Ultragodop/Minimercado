package com.project.minimercado.dto.payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private Cart cart;
    private Client client;
    private String callbackUrl;
    private String responseType = "Json";
}
