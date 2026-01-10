package com.manoj.finorder.orderservice.api;

import com.manoj.finorder.orderservice.model.Order;
import com.manoj.finorder.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrderRequest(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.created(URI.create("/orders"+order.getOrderId())).body(toOrderResponse(order));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable String orderId) {
        return orderService.confirmOrder(orderId)
                .map(order -> ResponseEntity.ok(toOrderResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId)
                .map(order -> ResponseEntity.ok(toOrderResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }
    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .orderItems(order.getItems())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}