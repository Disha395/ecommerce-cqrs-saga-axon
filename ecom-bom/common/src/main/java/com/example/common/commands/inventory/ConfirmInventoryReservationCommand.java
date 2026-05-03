package com.example.common.commands.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class ConfirmInventoryReservationCommand {

    @TargetAggregateIdentifier
    private final String inventoryId;
    private final String orderId;       // to find and confirm the right reservation
}
