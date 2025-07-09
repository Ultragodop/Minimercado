package com.project.minimercado.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class CallbackRequest {
    @JsonProperty("TransactionExternalId")
    private String transactionExternalId;

    @JsonProperty("PurchaseData")
    private PurchaseData purchaseData;

    @JsonProperty("InstrumentData")
    private InstrumentData instrumentData;


}