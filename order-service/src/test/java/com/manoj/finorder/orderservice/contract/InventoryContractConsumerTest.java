package com.manoj.finorder.orderservice.contract;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

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
    @BeforeEach
    void setup() {
        // Contract tests bootstrap the context; the generated tests drive messaging through Kafka.
    }
}
