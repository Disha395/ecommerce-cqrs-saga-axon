package com.example.order.rest;

import com.example.common.commands.order.CancelOrderCommand;
import com.example.common.commands.order.CreateOrderCommand;

import com.example.order.rest.dto.CreateOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Commands", description = "Endpoints for creating and cancelling orders")
public class OrderCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    @Operation(summary = "Place a new order")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        log.info("Dispatching CreateOrderCommand for orderId: {}", orderId);

        commandGateway.sendAndWait(
                CreateOrderCommand.builder()
                        .orderId(orderId)
                        .customerId(request.getCustomerId())
                        .shippingAddress(request.getShippingAddress())
                        .totalAmount(request.getTotalAmount())
                        .items(request.getItems().stream()
                                .map(item -> CreateOrderCommand.OrderItemDTO.builder()
                                        .productId(item.getProductId())
                                        .productName(item.getProductName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .subTotal(item.getSubTotal())
                                        .build())
                                .collect(java.util.stream.Collectors.toList()))
                        .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<String> cancelOrder(
            @PathVariable String orderId,
            @RequestParam String reason) {
        log.info("Dispatching CancelOrderCommand for orderId: {}", orderId);

        commandGateway.sendAndWait(
                new CancelOrderCommand(orderId, reason)
        );

        return ResponseEntity.ok("Order cancelled successfully");
    }
}