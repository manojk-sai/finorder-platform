package com.manoj.finorder.orderservice.event;

import com.manoj.finorder.orderservice.model.OrderItem;
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
    private String eventType;
    private String orderId;
    private String reason;
    private Instant occuredAt;
    private List<OrderItem> orderItems;
}