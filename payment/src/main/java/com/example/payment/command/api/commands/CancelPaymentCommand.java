package com.example.payment.command.api.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class CancelPaymentCommand {

    @TargetAggregateIdentifier
    private final String paymentId;
    private final String orderId;
    private final String reason;
}