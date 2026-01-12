package com.manoj.finorder.payledgerservice.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class CreatePaymentIntentRequest {
    @NotBlank
    private String orderId;
    @NonNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    @NotBlank
    private String currency;
}
