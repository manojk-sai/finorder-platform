package com.manoj.finorder.orderservice.ops;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DlqMessage {
    private String id;
    private String payload;
    private String topic;
    private String reason;
    private Instant createdAt;
}
