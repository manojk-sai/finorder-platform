package com.manoj.finorder.orderservice.event;

import com.manoj.finorder.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener{
    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topic.inventory-events:inventory-events}", groupId = "order-service-group")
    public void onInventoryEvent(InventoryEvent event) {
        log.info("Received inventory event: orderId={}, eventType={}", event.getOrderId(), event.getEventType());
        if (event == null || event.getEventType() == null) {return;}
        if("InventoryReserved".equals(event.getEventType())) {
            orderService.markReserved(event.getOrderId());
        } else if("InventoryReservationFailed".equals(event.getEventType())) {
            orderService.markReservationFailed(event.getOrderId());
        }
    }
}