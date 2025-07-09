package com.project.minimercado.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    @JsonProperty("Quantity")
    private BigDecimal quantity;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("TaxedAmount")
    private BigDecimal taxedAmount;

    @JsonProperty("Amount")
    private BigDecimal amount;


}