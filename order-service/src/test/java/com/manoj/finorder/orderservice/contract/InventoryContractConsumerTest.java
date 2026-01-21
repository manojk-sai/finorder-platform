package com.manoj.finorder.orderservice.contract;

import com.manoj.finorder.orderservice.event.InventoryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"+
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"+
                "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
@AutoConfigureStubRunner(
        ids = {"com.manoj.finorder:inventory-service:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@EmbeddedKafka(partitions = 1, topics = {"inventory-events" })
@ActiveProfiles("test")
public class InventoryContractConsumerTest {
    @Autowired
    private StubbedInventoryListener listener;

    @Autowired
    private StubTrigger stubTrigger;

    @Test
    void consumesInventoryReservedEventFromStub() throws InterruptedException {
        stubTrigger.trigger("inventory_reserved");
        // Wait for the listener to process the event
        Thread.sleep(2000);

        InventoryEvent event = listener.poll(Duration.ofSeconds(10));

    }

    @Configuration
    static class StubListenerConfig{
        @Bean
        public StubbedInventoryListener stubbedInventoryListener() {
            return new StubbedInventoryListener();
        }
    }

}
