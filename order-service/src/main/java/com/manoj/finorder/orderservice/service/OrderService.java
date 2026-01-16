package com.manoj.finorder.orderservice.service;

import com.manoj.finorder.orderservice.api.CreateOrderRequest;
import com.manoj.finorder.orderservice.event.InventoryEvent;
import com.manoj.finorder.orderservice.model.Order;
import com.manoj.finorder.orderservice.model.OrderStatus;
import com.manoj.finorder.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @Value("${app.kafka.topic.order-events:order-events}")
    private String orderEventsTopic;

    public Order createOrder(CreateOrderRequest orderRequest, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                Order order = existing.get();
                if (!matchesRequest(order, orderRequest)) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Idempotency key reuse with different payload"
                    );
                }
                log.info("order.idempotent_hit orderId: {} customerId: {}", order.getOrderId(), order.getCustomerId());
                return order;
            }
        }
        Instant now = Instant.now();
        Order order = Order.builder()
                .customerId(orderRequest.getCustomerId())
                .createdAt(now)
                .items(orderRequest.getOrderItems())
                .status(OrderStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .idempotencyKey(idempotencyKey)
                .build();
        Order savedOrder = orderRepository.save(order);
        log.info("order.created orderId: {} customerId: {} status: {}", savedOrder.getOrderId(), savedOrder.getCustomerId(), savedOrder.getStatus());
        return savedOrder;
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
            log.info("order.confirmed orderId:{} status:{}", saved.getOrderId(), saved.getStatus());

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
            Order saved = orderRepository.save(existing);
            log.info("order.reserved orderId={} status={}", saved.getOrderId(), saved.getStatus());
            return saved;
        });
    }

    public Optional<Order> markReservationFailed(String orderId) {
        return orderRepository.findById(orderId).map(existing -> {
            existing.setStatus(OrderStatus.RESERVATION_FAILED);
            existing.setUpdatedAt(Instant.now());
            Order saved = orderRepository.save(existing);
            log.info("order.reservation_failed orderId={} status={}", saved.getOrderId(), saved.getStatus());
            return saved;
        });
    }

    public Optional<Order> markPaid(String orderId) {
        return orderRepository.findById(orderId).map(existing -> {
            existing.setStatus(OrderStatus.PAID);
            existing.setUpdatedAt(Instant.now());
            Order saved = orderRepository.save(existing);
            log.info("order.paid orderId={} status={}", saved.getOrderId(), saved.getStatus());
            return saved;
        });
    }


    public Optional<Order> markPaymentFailed(String orderId) {
        return orderRepository.findById(orderId).map(existing -> {
            existing.setStatus(OrderStatus.PAYMENT_FAILED);
            existing.setUpdatedAt(Instant.now());
            Order saved = orderRepository.save(existing);
            log.info("order.payment_failed orderId={} status={}", saved.getOrderId(), saved.getStatus());
            return saved;
        });
    }

    private boolean matchesRequest(Order order, CreateOrderRequest orderRequest) {
        if(!order.getCustomerId().equals(orderRequest.getCustomerId())) {return false;}
        List<?> existingItems = order.getItems();
        List<?> requestItems = orderRequest.getOrderItems();
        return existingItems != null && existingItems.equals(requestItems);
    }

}