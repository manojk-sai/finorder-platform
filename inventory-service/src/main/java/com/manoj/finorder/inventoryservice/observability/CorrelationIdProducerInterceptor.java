package com.manoj.finorder.inventoryservice.observability;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class CorrelationIdProducerInterceptor implements ProducerInterceptor<String, Object> {
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> producerRecord) {
        if(producerRecord==null) return null;
        if(producerRecord.headers().lastHeader(CorrelationIdContext.HEADER_NAME) == null){
            String correlationId = CorrelationIdContext.get().orElseGet(() -> CorrelationIdContext.getOrCreate(null));
            producerRecord.headers().add(
                    CorrelationIdContext.HEADER_NAME,
                    correlationId.getBytes(StandardCharsets.UTF_8)
            );
        }
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
