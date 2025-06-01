package com.project.minimercado.dto.payment;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Product {
    private BigDecimal quantity;
    private String name;
    private BigDecimal taxedAmount;
    private BigDecimal amount;
} 