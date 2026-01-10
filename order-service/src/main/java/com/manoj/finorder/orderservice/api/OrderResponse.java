package com.manoj.finorder.orderservice.api;

import com.manoj.finorder.orderservice.model.OrderItem;
import com.manoj.finorder.orderservice.model.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Builder
@Value
public class OrderResponse {
    String orderId;
    String customerId;
    OrderStatus status;
    List<OrderItem> orderItems;
    Instant createdAt;
    Instant updatedAt;
}