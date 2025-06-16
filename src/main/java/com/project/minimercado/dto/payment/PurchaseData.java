package com.project.minimercado.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseData {
    private Integer status;
    private String created;
    private List<Product> products;
    private BigDecimal totalAmount;
    private BigDecimal taxedAmount;
    private Integer currency;
} 