package com.example.order.command.api.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String customerId;
    private final String shippingAddress;
    private final List<OrderItemDTO> items;
    private final BigDecimal totalAmount;

    @Data
    @Builder
    public static class OrderItemDTO {
        private final String productId;
        private final String productName;
        private final Integer quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal subTotal;
    }
}