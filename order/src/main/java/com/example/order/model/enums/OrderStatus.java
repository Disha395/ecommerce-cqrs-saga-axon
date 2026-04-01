package com.example.order.model.enums;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    PAYMENT_PROCESSING,
    PAYMENT_FAILED,
    CONFIRMED,
    CANCELLED
}
