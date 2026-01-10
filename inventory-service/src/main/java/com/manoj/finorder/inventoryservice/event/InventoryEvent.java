package com.manoj.finorder.inventoryservice.event;

import com.manoj.finorder.inventoryservice.model.InventoryItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEvent {
    private String eventType;
    private String orderId;
    private List<InventoryItem> items;
    private String reason;
    private Instant occuredAt;

}