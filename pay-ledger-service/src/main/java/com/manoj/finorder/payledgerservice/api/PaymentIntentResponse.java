package com.manoj.finorder.payledgerservice.api;

import com.manoj.finorder.payledgerservice.model.PaymentIntentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
@Data
@Builder
public class PaymentIntentResponse {
    private String id;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentIntentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
