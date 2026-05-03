package com.example.common.commands.inventory;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CreateInventoryCommand {
    @TargetAggregateIdentifier
    private final String inventoryId;
    private final String productId;
    private final String productName;
    private final Integer quantity;
}
