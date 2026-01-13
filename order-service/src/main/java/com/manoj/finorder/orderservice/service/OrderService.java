package com.manoj.finorder.orderservice.service;

import com.manoj.finorder.orderservice.api.CreateOrderRequest;
import com.manoj.finorder.orderservice.event.InventoryEvent;
import com.manoj.finorder.orderservice.model.Order;
import com.manoj.finorder.orderservice.model.OrderStatus;
import com.manoj.finorder.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @Value("${app.kafka.topic.order-events:order-events}")
    private String orderEventsTopic;

    public Order createOrder(CreateOrderRequest orderRequest) {
        Instant now = Instant.now();
        Order order = Order.builder()
                .customerId(orderRequest.getCustomerId())
                .createdAt(now)
                .items(orderRequest.getOrderItems())
                .status(OrderStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return orderRepository.save(order);
    }

    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }
    public Optional<Order> confirmOrder(String id) {
        return orderRepository.findById(id).map(existing -> {
            if (existing.getStatus() != OrderStatus.CREATED) {
                return existing;
            }
            existing.setStatus(OrderStatus.CONFIRMED);
            existing.setUpdatedAt(Instant.now());
            Order saved = orderRepository.save(existing);

            InventoryEvent event = InventoryEvent.builder()
                    .eventType("InventoryReserveRequested")
                    .orderId(saved.getOrderId())
                    .orderItems(saved.getItems())
                    .occuredAt(Instant.now())
                    .build();
            kafkaTemplate.send(orderEventsTopic, saved.getOrderId(), event);
            return saved;
        });
    }

    public Optional<Order> markReserved(String orderId) {
        log.info("Updating order {}", orderId);
        return orderRepository.findById(orderId).map(existing -> {
            existing.setStatus(OrderStatus.RESERVED);
            existing.setUpdatedAt(Instant.now());
            log.info("Order {} updated to status", orderId);
            return orderRepository.save(existing);
        });
    }

    public Optional<Order> markReservationFailed(String orderId) {
        return orderRepository.findById(orderId).map(existing -> {
            existing.setStatus(OrderStatus.RESERVATION_FAILED);
            existing.setUpdatedAt(Instant.now());
            return orderRepository.save(existing);
        });
    }

    public Optional<Order> markPaid(String orderId) {
       return orderRepository.findById(orderId).map(existing -> {
            existing.setStatus(OrderStatus.PAID);
            existing.setUpdatedAt(Instant.now());
            return orderRepository.save(existing);
        });
    }


    public Optional<Order> markPaymentFailed(String orderId) {
        return orderRepository.findById(orderId).map(existing -> {
            existing.setStatus(OrderStatus.PAYMENT_FAILED);
            existing.setUpdatedAt(Instant.now());
            return orderRepository.save(existing);
        });
    }
}