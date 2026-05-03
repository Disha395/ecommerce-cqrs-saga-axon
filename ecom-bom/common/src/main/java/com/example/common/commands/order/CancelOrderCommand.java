package com.example.common.commands.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class CancelOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String reason;        // why it was cancelled — useful for audit/saga compensation
}