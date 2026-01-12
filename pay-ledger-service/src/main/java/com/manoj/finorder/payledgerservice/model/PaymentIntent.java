package com.manoj.finorder.payledgerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("payment_intents")
public class PaymentIntent {
    @Id
    private String id;
    private String orderId;
    private String currency;
    private String idempotencyKey;
    private PaymentIntentStatus status;
    private BigDecimal amount;
    private Instant createdAt;
    private Instant updatedAt;
}
