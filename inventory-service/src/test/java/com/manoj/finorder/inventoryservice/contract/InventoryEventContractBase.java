package com.manoj.finorder.inventoryservice.contract;

import com.manoj.finorder.inventoryservice.event.InventoryEvent;
import com.manoj.finorder.inventoryservice.model.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration,"+
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
        "spring.cloud.contract.verifier.messaging.type=kafka",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "app.kafka.topic.order-events=order-events",
        "app.kafka.topic.inventory-events=inventory-events"
})
@AutoConfigureMessageVerifier
@EmbeddedKafka(partitions = 1, topics = { "order-events","inventory-events" })
@ActiveProfiles("test")
public class InventoryEventContractBase {
    @Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @BeforeEach
    void setup() {
        // Contract tests bootstrap the context; the generated tests drive messaging through Kafka.
    }

    public void triggerInventoryReserveRequested(){
        // This method triggers the inventory reservation logic
        InventoryEvent event = InventoryEvent.builder()
                .eventType("InventoryReserveRequested")
                .orderId("order-123")
                .orderItems(List.of(InventoryItem.builder()
                        .sku("sku-1")
                        .quantity(2)
                        .build()))
                .occuredAt(Instant.now())
                .build();
        kafkaTemplate.send("order-events", event.getOrderId(), event);
    }
}
