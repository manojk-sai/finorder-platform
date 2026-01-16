package com.manoj.finorder.inventoryservice.observability;

import com.manoj.finorder.inventoryservice.event.InventoryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.RecordInterceptor;

public class CorrelationIdRecordInterceptor implements RecordInterceptor<String, InventoryEvent> {

    @Override
    public ConsumerRecord<String, InventoryEvent> intercept(ConsumerRecord<String, InventoryEvent> record, Consumer<String, InventoryEvent> consumer) {
        Headers headers = record.headers();
        String correlationId = CorrelationIdContext.fromHeaders(headers);
        CorrelationIdContext.set(correlationId);
        return record;
    }

    @Override
    public void afterRecord(ConsumerRecord<String, InventoryEvent> record, Consumer<String, InventoryEvent> consumer) {
        CorrelationIdContext.clear();
    }
}
