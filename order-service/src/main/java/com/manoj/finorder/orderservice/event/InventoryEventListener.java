package com.manoj.finorder.orderservice.event;

import com.manoj.finorder.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
@Component
@RequiredArgsConstructor
public class InventoryEventListener{
    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topic.inventory-events:inventory-events}")
    public void onInventoryEvent(InventoryEvent event) {
        if (event == null || event.getEventType() == null) {return;}
        if("InventoryReserved".equals(event.getEventType())) {orderService.markReserved(event.getOrderId());}
    }
}