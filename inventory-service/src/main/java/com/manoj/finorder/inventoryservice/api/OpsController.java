package com.manoj.finorder.inventoryservice.api;

import com.manoj.finorder.inventoryservice.ops.ChaosService;
import com.manoj.finorder.inventoryservice.ops.CrashRequest;
import com.manoj.finorder.inventoryservice.ops.FailNextRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/ops/chaos")
@RequiredArgsConstructor
public class OpsController {
    private final ChaosService chaosService;

    @PostMapping("/inventory/fail-next")
    public ResponseEntity<Void> failNextReservation(@RequestBody FailNextRequest request) {
        chaosService.failNextReservation(request.getReason());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/consumer-crash")
    public ResponseEntity<Void> crashConsumer(@RequestBody CrashRequest request) {
        chaosService.crashFor(Duration.ofSeconds(request.getDurationSeconds()));
        return ResponseEntity.accepted().build();
    }
}
