package com.example.common.events.inventory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryCreatedEvent {
    private final String inventoryId;
    private final String productId;
    private final String productName;
    private final Integer quantity;

}
