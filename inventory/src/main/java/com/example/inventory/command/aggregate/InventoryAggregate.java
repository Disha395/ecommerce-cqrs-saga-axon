package com.example.inventory.command.aggregate;

import com.example.common.commands.inventory.ConfirmInventoryReservationCommand;
import com.example.common.commands.inventory.CreateInventoryCommand;
import com.example.common.commands.inventory.ReleaseInventoryCommand;
import com.example.common.commands.inventory.ReserveInventoryCommand;
import com.example.common.events.inventory.*;
import com.example.inventory.model.enums.InventoryStatus;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

@Aggregate
@NoArgsConstructor
@Slf4j
public class InventoryAggregate {

    @AggregateIdentifier
    private String inventoryId;
    private String productId;
    private String productName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private InventoryStatus status;

    // Command Handlers

    @CommandHandler
    public InventoryAggregate(CreateInventoryCommand command) {
        log.info("Handling CreateInventoryCommand for productId: {}",
                command.getProductId());

        if (command.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        AggregateLifecycle.apply(
                InventoryCreatedEvent.builder()
                        .inventoryId(command.getInventoryId())
                        .productId(command.getProductId())
                        .productName(command.getProductName())
                        .quantity(command.getQuantity())
                        .build()
        );
    }

    @CommandHandler
    public void handle(ReserveInventoryCommand command) {
        log.info("Handling ReserveInventoryCommand for orderId: {}, productId: {}",
                command.getOrderId(), command.getProductId());

        // check if enough stock is available
        if (this.availableQuantity - this.reservedQuantity < command.getQuantity()) {
            log.warn("Insufficient stock for productId: {}, available: {}, requested: {}",
                    this.productId,
                    this.availableQuantity - this.reservedQuantity,
                    command.getQuantity());

            AggregateLifecycle.apply(
                    InventoryReservationFailedEvent.builder()
                            .inventoryId(this.inventoryId)
                            .productId(this.productId)
                            .orderId(command.getOrderId())
                            .reason("Insufficient stock for productId: " + this.productId)
                            .build()
            );
            return;
        }

        AggregateLifecycle.apply(
                InventoryReservedEvent.builder()
                        .inventoryId(this.inventoryId)
                        .productId(this.productId)
                        .orderId(command.getOrderId())
                        .reservationId(UUID.randomUUID().toString())
                        .quantity(command.getQuantity())
                        .build()
        );
    }

    @CommandHandler
    public void handle(ReleaseInventoryCommand command) {
        log.info("Handling ReleaseInventoryCommand for orderId: {}",
                command.getOrderId());

        if (this.reservedQuantity <= 0) {
            throw new IllegalStateException(
                    "No reserved quantity to release for orderId: " + command.getOrderId()
            );
        }

        AggregateLifecycle.apply(
                InventoryReleasedEvent.builder()
                        .inventoryId(this.inventoryId)
                        .orderId(command.getOrderId())
                        .quantity(this.reservedQuantity)
                        .build()
        );
    }

    @CommandHandler
    public void handle(ConfirmInventoryReservationCommand command) {
        log.info("Handling ConfirmInventoryReservationCommand for orderId: {}",
                command.getOrderId());

        if (this.reservedQuantity <= 0) {
            throw new IllegalStateException(
                    "No reserved quantity to confirm for orderId: " + command.getOrderId()
            );
        }

        AggregateLifecycle.apply(
                InventoryReservationConfirmedEvent.builder()
                        .inventoryId(this.inventoryId)
                        .orderId(command.getOrderId())
                        .quantity(this.reservedQuantity)
                        .build()
        );
    }

    // Event Sourcing Handlers

    @EventSourcingHandler
    public void on(InventoryCreatedEvent event) {
        this.inventoryId = event.getInventoryId();
        this.productId = event.getProductId();
        this.productName = event.getProductName();
        this.availableQuantity = event.getQuantity();
        this.reservedQuantity = 0;
        this.status = InventoryStatus.AVAILABLE;
        log.info("InventoryCreatedEvent sourced, inventoryId: {}", this.inventoryId);
    }

    @EventSourcingHandler
    public void on(InventoryReservedEvent event) {
        this.reservedQuantity += event.getQuantity();
        if (this.availableQuantity - this.reservedQuantity == 0) {
            this.status = InventoryStatus.DEPLETED;
        } else {
            this.status = InventoryStatus.RESERVED;
        }
        log.info("InventoryReservedEvent sourced, reservedQuantity: {}",
                this.reservedQuantity);
    }

    @EventSourcingHandler
    public void on(InventoryReservationFailedEvent event) {
        // no state change — stock wasn't touched
        log.info("InventoryReservationFailedEvent sourced for orderId: {}",
                event.getOrderId());
    }

    @EventSourcingHandler
    public void on(InventoryReleasedEvent event) {
        this.reservedQuantity -= event.getQuantity();
        this.status = InventoryStatus.AVAILABLE;
        log.info("InventoryReleasedEvent sourced, reservedQuantity: {}",
                this.reservedQuantity);
    }

    @EventSourcingHandler
    public void on(InventoryReservationConfirmedEvent event) {
        this.availableQuantity -= event.getQuantity();
        this.reservedQuantity -= event.getQuantity();
        if (this.availableQuantity == 0) {
            this.status = InventoryStatus.DEPLETED;
        } else {
            this.status = InventoryStatus.AVAILABLE;
        }
        log.info("InventoryReservationConfirmedEvent sourced, availableQuantity: {}",
                this.availableQuantity);
    }
}