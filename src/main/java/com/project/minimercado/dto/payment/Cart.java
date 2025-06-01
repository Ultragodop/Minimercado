package com.project.minimercado.dto.payment;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {
    private Integer invoiceNumber;
    private Integer currency = 858; // Peso Uruguayo por defecto
    private BigDecimal taxedAmount;
    private BigDecimal totalAmount;
    private List<Product> products;
    private String linkImageUrl;
    private String transactionExternalId;
} 