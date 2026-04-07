// rest/dto/CreateOrderRequest.java
package com.example.order.rest.dto;

import com.example.order.command.api.commands.CreateOrderCommand;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {

    private String customerId;
    private String shippingAddress;
    private List<CreateOrderCommand.OrderItemDTO> items;
    private BigDecimal totalAmount;
}