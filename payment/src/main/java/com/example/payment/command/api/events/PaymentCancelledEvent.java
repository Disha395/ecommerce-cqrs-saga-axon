package com.example.payment.command.api.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCancelledEvent {

    private final String paymentId;
    private final String orderId;
    private final String reason;
}