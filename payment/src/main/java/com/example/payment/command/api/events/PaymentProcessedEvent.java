package com.example.payment.command.api.events;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentProcessedEvent {

    private final String paymentId;
    private final String orderId;
    private final String customerId;
    private final BigDecimal amount;
}