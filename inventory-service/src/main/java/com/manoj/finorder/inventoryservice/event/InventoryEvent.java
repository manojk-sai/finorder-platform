package com.manoj.finorder.inventoryservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.manoj.finorder.inventoryservice.model.InventoryItem;
import com.mongodb.lang.Nullable;
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
    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("orderItems")
    @Nullable
    private List<InventoryItem> orderItems;
    @JsonProperty("reason")
    @Nullable
    private String reason;
    @JsonProperty("occuredAt")
    private Instant occuredAt;

}