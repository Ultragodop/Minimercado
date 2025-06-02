package com.project.minimercado.dto.payment;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PaymentRequest {
    private Cart cart;
    private Client client;
    private String callbackUrl;
    private String responseType = "Json";
}
