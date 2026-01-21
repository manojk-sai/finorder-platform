package com.manoj.finorder.orderservice.contract;

import com.manoj.finorder.orderservice.event.InventoryEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StubbedInventoryListener {
    private final BlockingQueue<InventoryEvent> events = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "inventory-events", containerFactory = "inventoryKafkaListenerContainerFactory")
    void handle(InventoryEvent event) {
        events.add(event);
    }

    InventoryEvent poll(Duration timeout) throws InterruptedException {
        return events.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
}
