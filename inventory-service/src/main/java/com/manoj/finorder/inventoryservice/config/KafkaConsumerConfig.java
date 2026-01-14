package com.manoj.finorder.inventoryservice.config;

import com.manoj.finorder.inventoryservice.event.InventoryEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, InventoryEvent> consumerFactory,
            CommonErrorHandler commonErrorHandler
            ) {
        ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(commonErrorHandler);
        return factory;
    }
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxInterval(10000L);
        return new DefaultErrorHandler(backOff);
    }

}
