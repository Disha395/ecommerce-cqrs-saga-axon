package com.example.common.events.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderConfirmedEvent {

    private final String orderId;
}
