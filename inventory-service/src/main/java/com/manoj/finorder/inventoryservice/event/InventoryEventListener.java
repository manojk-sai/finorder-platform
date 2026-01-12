package com.manoj.finorder.inventoryservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class InventoryEventListener {
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    @Value("${app.kafka.topic.inventory-events:inventory-events}")
    private String inventoryEventsTopic;
    @KafkaListener(topics = "${app.kafka.topic.inventory-events:inventory-events}")
    public void onInventoryEvent(InventoryEvent event) {
        if (event == null || event.getEventType() == null) {return;}
        if(!"InventoryReserveRequested".equals(event.getEventType())) {return;}
        boolean valid = event.getOrderItems() != null && !event.getOrderItems().isEmpty();
        InventoryEvent response = InventoryEvent.builder()
                .eventType(valid?"InventoryReserved":"InventoryReservationFailed")
                .orderId(event.getOrderId())
                .orderItems(event.getOrderItems())
                .reason(valid?null:"Invalid Quantity")
                .occuredAt(Instant.now())
                .build();
        kafkaTemplate.send(inventoryEventsTopic, event.getOrderId(), response);
    }

}