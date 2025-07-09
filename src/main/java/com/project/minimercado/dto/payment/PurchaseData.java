package com.project.minimercado.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseData {
    @JsonProperty("Status")
    private Integer status;

    @JsonProperty("Created")
    private String created;

    @JsonProperty("Products")
    private List<Product> products;

    @JsonProperty("TotalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("TaxedAmount")
    private BigDecimal taxedAmount;

    @JsonProperty("Currency")
    private Integer currency;

}