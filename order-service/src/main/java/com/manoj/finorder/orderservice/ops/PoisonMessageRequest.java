package com.manoj.finorder.orderservice.ops;

import lombok.Data;

@Data
public class PoisonMessageRequest {
    private String topic;
    private String payload;
}
