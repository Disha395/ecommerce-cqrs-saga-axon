package com.example.order.query.handler;

import com.example.order.model.entity.OrderEntity;
import com.example.order.model.entity.OrderItemEntity;
import com.example.order.query.api.queries.GetAllOrdersQuery;
import com.example.order.query.api.queries.GetOrderByIdQuery;
import com.example.order.query.api.response.OrderItemResponse;
import com.example.order.query.api.response.OrderResponse;
import com.example.order.query.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderQueryHandler {

    private final OrderRepository orderRepository;

    @QueryHandler
    public OrderResponse handle(GetOrderByIdQuery query) {
        log.info("Handling GetOrderByIdQuery for orderId: {}", query.getOrderId());

        OrderEntity order = orderRepository.findById(query.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found, orderId: " + query.getOrderId()));

        return mapToResponse(order);
    }

    @QueryHandler
    public List<OrderResponse> handle(GetAllOrdersQuery query) {
        log.info("Handling GetAllOrdersQuery for customerId: {}", query.getCustomerId());

        return orderRepository.findByCustomerId(query.getCustomerId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(OrderEntity order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .shippingAddress(order.getShippingAddress())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapItemToResponse(OrderItemEntity item) {
        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subTotal(item.getSubTotal())
                .build();
    }
}