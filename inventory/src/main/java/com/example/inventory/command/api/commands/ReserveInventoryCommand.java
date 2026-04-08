package com.example.inventory.command.api.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class ReserveInventoryCommand {

    @TargetAggregateIdentifier
    private final String inventoryId;
    private final String orderId;
    private final String productId;
    private final Integer quantity;

}
