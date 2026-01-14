package com.manoj.finorder.inventoryservice.event;

import com.manoj.finorder.inventoryservice.ops.ChaosService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryEventListener {
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    private final ChaosService chaosService;
    @Value("${app.kafka.topic.order-events:order-events}")
    private String orderEventsTopic;
    @Value("${app.kafka.topic.inventory-events:inventory-events}")
    private String inventoryEventsTopic;
    @KafkaListener(topics = "${app.kafka.topic.order-events:order-events}")
    public void onInventoryEvent(InventoryEvent event) {
        if (event == null || event.getEventType() == null) {return;}
        if(!"InventoryReserveRequested".equals(event.getEventType())) {return;}
        if(chaosService.isCrashActive()){ throw new IllegalStateException("Inventory consumer crash simulation"); }
        Optional<String> forcedFailure = chaosService.consumeFailureReason();
        boolean valid = event.getOrderItems() != null && !event.getOrderItems().isEmpty();
        boolean shouldFail = forcedFailure.isPresent() || !valid;
        String reason = forcedFailure.orElse(valid ? null : "Invalid Quantity");
        InventoryEvent response = InventoryEvent.builder()
                .eventType(shouldFail ? "InventoryReservationFailed":"InventoryReserved")
                .orderId(event.getOrderId())
                .orderItems(event.getOrderItems())
                .reason(reason)
                .occuredAt(Instant.now())
                .build();
        kafkaTemplate.send(inventoryEventsTopic, event.getOrderId(), response);
    }

}