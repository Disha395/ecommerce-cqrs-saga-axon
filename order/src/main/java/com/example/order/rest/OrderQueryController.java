// rest/OrderQueryController.java
package com.example.order.rest;

import com.example.order.query.api.queries.GetAllOrdersQuery;
import com.example.order.query.api.queries.GetOrderByIdQuery;
import com.example.order.query.api.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Queries", description = "Endpoints for fetching orders")
public class OrderQueryController {

    private final QueryGateway queryGateway;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public CompletableFuture<OrderResponse> getOrderById(@PathVariable String orderId) {
        log.info("Dispatching GetOrderByIdQuery for orderId: {}", orderId);
        return queryGateway.query(
                new GetOrderByIdQuery(orderId),
                OrderResponse.class
        );
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all orders for a customer")
    public CompletableFuture<List<OrderResponse>> getOrdersByCustomer(
            @PathVariable String customerId) {
        log.info("Dispatching GetAllOrdersQuery for customerId: {}", customerId);
        return queryGateway.query(
                new GetAllOrdersQuery(customerId),
                ResponseTypes.multipleInstancesOf(OrderResponse.class)
        );
    }
}