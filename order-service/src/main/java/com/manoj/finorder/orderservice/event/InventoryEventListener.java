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

    @KafkaListener(
            topics = "${app.kafka.topic.inventory-events:inventory-events}",
            groupId = "order-service-group",
            containerFactory = "inventoryKafkaListenerContainerFactory")
    public void onInventoryEvent(InventoryEvent event) {
        if (event == null || event.getEventType() == null) {return;}
        if("InventoryReserved".equals(event.getEventType())) {
            log.info("inventory_event.reserved orderId: {}", event.getOrderId());
            orderService.markReserved(event.getOrderId());
        } else if("InventoryReservationFailed".equals(event.getEventType())) {
            log.info("inventory_event.reservation_failed orderId: {} reason: {}", event.getOrderId(), event.getReason());
            orderService.markReservationFailed(event.getOrderId());
        }
    }
}