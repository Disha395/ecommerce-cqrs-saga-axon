package com.example.order.query.projection;

import com.example.order.command.api.events.OrderCancelledEvent;
import com.example.order.command.api.events.OrderConfirmedEvent;
import com.example.order.command.api.events.OrderCreatedEvent;
import com.example.order.model.entity.OrderEntity;
import com.example.order.model.entity.OrderItemEntity;
import com.example.order.model.enums.OrderStatus;
import com.example.order.query.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProjection {

    private final OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Projecting OrderCreatedEvent for orderId: {}", event.getOrderId());

        List<OrderItemEntity> items = event.getItems().stream()
                .map(item -> OrderItemEntity.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subTotal(item.getSubTotal())
                        .build())
                .collect(Collectors.toList());

        OrderEntity order = OrderEntity.builder()
                .orderId(event.getOrderId())
                .customerId(event.getCustomerId())
                .shippingAddress(event.getShippingAddress())
                .totalAmount(event.getTotalAmount())
                .status(OrderStatus.CREATED)
                .build();

        // set the back-reference on each item
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        orderRepository.save(order);
        log.info("Order saved to read DB, orderId: {}", event.getOrderId());
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        log.info("Projecting OrderConfirmedEvent for orderId: {}", event.getOrderId());

        OrderEntity order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("Projecting OrderCancelledEvent for orderId: {}", event.getOrderId());

        OrderEntity order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private OrderEntity getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException(
                        "Order not found in read DB, orderId: " + orderId));
    }
}