package com.manoj.finorder.orderservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.manoj.finorder.orderservice.model.OrderItem;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("reason")
    @Nullable
    private String reason;
    @JsonProperty("occuredAt")
    private Instant occuredAt;
    @JsonProperty("orderItems")
    @Nullable
    private List<OrderItem> orderItems;
}