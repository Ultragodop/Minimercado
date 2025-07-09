package com.project.minimercado.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InstrumentData {
    @JsonProperty("Expiration")
    private String expiration;
} 