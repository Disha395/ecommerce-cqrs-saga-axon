package com.example.inventory.command.api.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryReservedEvent {
    private final String inventoryId;
    private final String productId;
    private final String orderId;
    private final String reservationId; //used by projection
    private final Integer quantity;

}
