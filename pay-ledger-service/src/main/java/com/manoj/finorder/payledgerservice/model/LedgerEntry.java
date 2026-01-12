package com.manoj.finorder.payledgerservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry {
    @Id
    private String id;
    private String paymentIntentId;
    private String orderId;
    private String account;
    private String currency;
    private LedgerEntryType entryType;
    private BigDecimal amount;
    private Instant createdAt;
}
