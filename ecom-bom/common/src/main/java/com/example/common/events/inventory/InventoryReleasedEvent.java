package com.example.common.events.inventory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryReleasedEvent {
    private final String inventoryId;
    private final String orderId;
    private final Integer quantity;
}
