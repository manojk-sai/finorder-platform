package com.manoj.finorder.inventoryservice.config;

import com.manoj.finorder.inventoryservice.event.InventoryEvent;
import com.manoj.finorder.inventoryservice.observability.CorrelationIdRecordInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, InventoryEvent> consumerFactory,
            CommonErrorHandler errorHandler
            ) {
        ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.setRecordInterceptor(correlationIdRecordInterceptor());
        return factory;
    }
    @Bean
    public CorrelationIdRecordInterceptor correlationIdRecordInterceptor() {
        return new CorrelationIdRecordInterceptor();
    }
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<?, ?> kafkaTemplate){
        ExponentialBackOff backOff = new ExponentialBackOff();
        return new DefaultErrorHandler( new DeadLetterPublishingRecoverer(kafkaTemplate), backOff);
    }
}
