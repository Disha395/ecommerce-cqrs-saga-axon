package com.example.order.command.aggregate;

import com.example.common.commands.order.CancelOrderCommand;
import com.example.common.commands.order.ConfirmOrderCommand;
import com.example.common.commands.order.CreateOrderCommand;
import com.example.common.events.order.OrderCancelledEvent;
import com.example.common.events.order.OrderConfirmedEvent;
import com.example.common.events.order.OrderCreatedEvent;
import com.example.order.model.enums.OrderStatus;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.util.List;

@Aggregate
@NoArgsConstructor                      // required by Axon for event sourcing reconstruction
@Slf4j
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String customerId;
    private String shippingAddress;
    private List<OrderCreatedEvent.OrderItemDTO> items;
    private BigDecimal totalAmount;
    private OrderStatus status;

    // ─── Command Handlers ───────────────────────────────────────────────────

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("Handling CreateOrderCommand for orderId: {}", command.getOrderId());

        // validate
        if (command.getItems() == null || command.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (command.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }

        AggregateLifecycle.apply(
                OrderCreatedEvent.builder()
                        .orderId(command.getOrderId())
                        .customerId(command.getCustomerId())
                        .shippingAddress(command.getShippingAddress())
                        .items(command.getItems().stream()
                                .map(item -> OrderCreatedEvent.OrderItemDTO.builder()
                                        .productId(item.getProductId())
                                        .productName(item.getProductName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .subTotal(item.getSubTotal())
                                        .build())
                                .collect(java.util.stream.Collectors.toList()))
                        .totalAmount(command.getTotalAmount())
                        .build()
        );
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        log.info("Handling ConfirmOrderCommand for orderId: {}", command.getOrderId());

        if (this.status != OrderStatus.CREATED &&
                this.status != OrderStatus.INVENTORY_RESERVED) {
            throw new IllegalStateException(
                    "Order cannot be confirmed in status: " + this.status
            );
        }

        AggregateLifecycle.apply(
                new OrderConfirmedEvent(command.getOrderId())
        );
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        log.info("Handling CancelOrderCommand for orderId: {}", command.getOrderId());

        if (this.status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel an already confirmed order");
        }

        AggregateLifecycle.apply(
                new OrderCancelledEvent(command.getOrderId(), command.getReason())
        );
    }

    // ─── Event Sourcing Handlers ─────────────────────────────────────────────

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.shippingAddress = event.getShippingAddress();
        this.items = event.getItems();
        this.totalAmount = event.getTotalAmount();
        this.status = OrderStatus.CREATED;
        log.info("OrderCreatedEvent sourced, orderId: {}", this.orderId);
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        this.status = OrderStatus.CONFIRMED;
        log.info("OrderConfirmedEvent sourced, orderId: {}", this.orderId);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.status = OrderStatus.CANCELLED;
        log.info("OrderCancelledEvent sourced, orderId: {}", this.orderId);
    }
}
