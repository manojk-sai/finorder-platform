package com.manoj.finorder.payledgerservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("paymentIntentId")
    private String paymentIntentId;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("occuredAt")
    private Instant occuredAt;
}
