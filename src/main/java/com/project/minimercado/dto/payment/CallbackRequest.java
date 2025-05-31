package com.project.minimercado.dto.payment;

import lombok.Data;

@Data
public class CallbackRequest {
    private String transactionExternalId;
    private PurchaseData purchaseData;
    private InstrumentData instrumentData;
} 