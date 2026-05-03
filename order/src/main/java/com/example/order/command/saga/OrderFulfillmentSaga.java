package com.example.order.command.saga;

import com.example.common.commands.inventory.ConfirmInventoryReservationCommand;
import com.example.common.commands.inventory.ReleaseInventoryCommand;
import com.example.common.commands.inventory.ReserveInventoryCommand;
import com.example.common.commands.order.CancelOrderCommand;
import com.example.common.commands.order.ConfirmOrderCommand;
import com.example.common.commands.payment.ProcessPaymentCommand;
import com.example.common.dto.inventory.InventoryResponse;
import com.example.common.events.inventory.InventoryReservationFailedEvent;
import com.example.common.events.inventory.InventoryReservedEvent;
import com.example.common.events.order.OrderCancelledEvent;
import com.example.common.events.order.OrderConfirmedEvent;
import com.example.common.events.order.OrderCreatedEvent;
import com.example.common.events.payment.PaymentFailedEvent;
import com.example.common.events.payment.PaymentProcessedEvent;
import com.example.common.queries.inventory.GetInventoryByProductIdQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Saga
@Slf4j
@ProcessingGroup("order-saga-group")
public class OrderFulfillmentSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    // ─── Saga State ──────────────────────────────────────────────────────────
    private String orderId;
    private String customerId;
    private String paymentId;
    private BigDecimal totalAmount;
    private int totalItems;
    private int reservedCount;
    private Map<String, Integer> reservedInventories = new HashMap<>();

    // ─── START ───────────────────────────────────────────────────────────────

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        log.info("Saga started for orderId: {}", event.getOrderId());

        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.totalAmount = event.getTotalAmount();
        this.totalItems = event.getItems().size();
        this.reservedCount = 0;

        for (OrderCreatedEvent.OrderItemDTO item : event.getItems()) {
            try {
                InventoryResponse inventory = queryGateway.query(
                        new GetInventoryByProductIdQuery(item.getProductId()),
                        ResponseTypes.instanceOf(InventoryResponse.class)
                ).join();

                if (inventory == null) {
                    log.warn("Inventory not found for productId: {}",
                            item.getProductId());
                    cancelOrder("Inventory not found for productId: "
                            + item.getProductId());
                    return;
                }

                log.info("Sending ReserveInventoryCommand for inventoryId: {}",
                        inventory.getInventoryId());

                commandGateway.send(
                        ReserveInventoryCommand.builder()
                                .inventoryId(inventory.getInventoryId())
                                .orderId(this.orderId)
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build()
                );

            } catch (Exception e) {
                log.error("Error querying inventory for productId: {}",
                        item.getProductId(), e);
                cancelOrder("Inventory not found for productId: "
                        + item.getProductId());
                return;
            }
        }
    }

    // ─── INVENTORY RESERVED ──────────────────────────────────────────────────

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservedEvent event) {
        log.info("Inventory reserved for orderId: {}, inventoryId: {}",
                event.getOrderId(), event.getInventoryId());

        reservedCount++;
        reservedInventories.put(event.getInventoryId(), event.getQuantity());

        if (reservedCount == totalItems) {
            log.info("All {} items reserved, proceeding to payment", totalItems);
            processPayment();
        }
    }

    // ─── INVENTORY RESERVATION FAILED ───────────────────────────────────────

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservationFailedEvent event) {
        log.warn("Inventory reservation failed for orderId: {}, reason: {}",
                event.getOrderId(), event.getReason());

        releaseAllInventories("Inventory reservation failed: " + event.getReason());
        cancelOrder("Inventory reservation failed: " + event.getReason());
    }

    // ─── PAYMENT PROCESSED ───────────────────────────────────────────────────

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentProcessedEvent event) {
        log.info("Payment processed for orderId: {}, paymentId: {}",
                event.getOrderId(), event.getPaymentId());

        reservedInventories.forEach((inventoryId, quantity) -> {
            log.info("Confirming inventory for inventoryId: {}", inventoryId);
            commandGateway.send(
                    new ConfirmInventoryReservationCommand(
                            inventoryId,
                            this.orderId
                    )
            );
        });

        log.info("Confirming order, orderId: {}", orderId);
        commandGateway.send(new ConfirmOrderCommand(this.orderId));
    }

    // ─── PAYMENT FAILED ──────────────────────────────────────────────────────

    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentFailedEvent event) {
        log.warn("Payment failed for orderId: {}, reason: {}",
                event.getOrderId(), event.getReason());

        releaseAllInventories("Payment failed: " + event.getReason());
        cancelOrder("Payment failed: " + event.getReason());
    }

    // ─── ORDER CONFIRMED — END SAGA ──────────────────────────────────────────

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderConfirmedEvent event) {
        log.info("Saga ended — order confirmed! orderId: {}", event.getOrderId());
    }

    // ─── ORDER CANCELLED — END SAGA ──────────────────────────────────────────

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCancelledEvent event) {
        log.info("Saga ended — order cancelled, orderId: {}", event.getOrderId());
    }

    // ─── Helper Methods ──────────────────────────────────────────────────────

    private void processPayment() {
        this.paymentId = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("paymentId", this.paymentId);

        log.info("Sending ProcessPaymentCommand, paymentId: {}", paymentId);

        commandGateway.send(
                ProcessPaymentCommand.builder()
                        .paymentId(this.paymentId)
                        .orderId(this.orderId)
                        .customerId(this.customerId)
                        .amount(this.totalAmount)
                        .build()
        );
    }

    private void releaseAllInventories(String reason) {
        reservedInventories.forEach((inventoryId, quantity) ->
                commandGateway.send(
                        new ReleaseInventoryCommand(
                                inventoryId,
                                this.orderId,
                                reason
                        )
                )
        );
    }

    private void cancelOrder(String reason) {
        commandGateway.send(
                new CancelOrderCommand(
                        this.orderId,
                        reason
                )
        );
    }
}