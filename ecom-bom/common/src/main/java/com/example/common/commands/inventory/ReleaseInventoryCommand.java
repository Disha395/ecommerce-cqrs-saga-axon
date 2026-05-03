package com.example.common.commands.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class ReleaseInventoryCommand {

    @TargetAggregateIdentifier
    private final String inventoryId;
    private final String orderId; //to find and release the right reservation
    private final String reason; //why it was released - saga compensation
}
