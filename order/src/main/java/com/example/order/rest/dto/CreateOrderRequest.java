package com.example.order.rest.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    private String customerId;
    private String shippingAddress;
    private List<OrderItemRequest> items;
    private BigDecimal totalAmount;

    @Data
    public static class OrderItemRequest {
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subTotal;
    }
}