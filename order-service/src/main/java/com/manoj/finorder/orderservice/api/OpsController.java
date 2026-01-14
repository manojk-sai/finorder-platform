package com.manoj.finorder.orderservice.api;

import com.manoj.finorder.orderservice.ops.DlqMessage;
import com.manoj.finorder.orderservice.ops.DlqService;
import com.manoj.finorder.orderservice.ops.PoisonMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/ops")
@RequiredArgsConstructor
public class OpsController {
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final DlqService dlqService;

    @PostMapping("/chaos/poison-message")
    public ResponseEntity<DlqMessage> sendPoisonMessage(@RequestBody PoisonMessageRequest request) {
        if (request.getTopic() == null || request.getTopic().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "topic is required");
        }
        if (request.getPayload() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payload is required");
        }
        stringKafkaTemplate.send(request.getTopic(), request.getPayload());
        DlqMessage message = dlqService.add(request.getTopic(), request.getPayload(), "Injected poison message");
        return ResponseEntity.accepted().body(message);
    }

    @GetMapping("/dlq")
    public List<DlqMessage> listDlqMessages() {
        return dlqService.list();
    }

    @PostMapping("/dlq/{id}/replay")
    public ResponseEntity<DlqMessage> replayDlqMessage(@PathVariable String id) {
        DlqMessage message = dlqService.find(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DLQ message not found"));
        stringKafkaTemplate.send(message.getTopic(), message.getPayload());
        dlqService.remove(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/dlq/replay")
    public List<DlqMessage> replayAllDlqMessages() {
        List<DlqMessage> messages = dlqService.list();
        messages.forEach(message ->
                stringKafkaTemplate.send(message.getTopic(), message.getPayload()));
        dlqService.clear();
        return messages;
    }
}