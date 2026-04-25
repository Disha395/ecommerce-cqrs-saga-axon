package com.example.payment.command.api.events;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentFailedEvent {

    private final String paymentId;
    private final String orderId;
    private final String customerId;
    private final BigDecimal amount;
    private final String reason;        // why payment failed — Saga needs this
}