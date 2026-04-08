package com.example.inventory.command.api.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryReservationFailedEvent {
    private final String inventoryId;
    private final String productId;
    private final String orderId;
    private final String reason; //not enough stock ,product not found etc
}
