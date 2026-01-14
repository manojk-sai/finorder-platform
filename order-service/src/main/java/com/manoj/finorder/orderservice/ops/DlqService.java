package com.manoj.finorder.orderservice.ops;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DlqService {
    private final Map<String, DlqMessage> messages = new ConcurrentHashMap<>();

    public DlqMessage add(String topic, String payload, String reason) {
        String id = UUID.randomUUID().toString();
        DlqMessage message = DlqMessage.builder()
                .id(id)
                .topic(topic)
                .payload(payload)
                .reason(reason)
                .createdAt(Instant.now())
                .build();
        messages.put(id, message);
        return message;
    }

    public List<DlqMessage> list() {
        return messages.values().stream()
                .sorted(Comparator.comparing(DlqMessage::getCreatedAt))
                .toList();
    }

    public Optional<DlqMessage> find(String id) {
        return Optional.ofNullable(messages.get(id));
    }

    public void remove(String id) {
        messages.remove(id);
    }

    public void clear(){
        messages.clear();
    }
}
