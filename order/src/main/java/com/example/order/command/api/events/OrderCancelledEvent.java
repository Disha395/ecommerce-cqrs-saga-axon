package com.example.order.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderCancelledEvent {

    private final String orderId;
    private final String reason;
}
