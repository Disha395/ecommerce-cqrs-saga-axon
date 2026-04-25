package com.example.inventory.query.projection;

import com.example.inventory.command.api.events.*;
import com.example.inventory.model.enity.InventoryEntity;
import com.example.inventory.model.enity.InventoryReservationEntity;
import com.example.inventory.model.enums.InventoryStatus;
import com.example.inventory.model.enums.ReservationStatus;
import com.example.inventory.query.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ProcessingGroup("inventory-group")
public class InventoryProjection {
    private final InventoryRepository inventoryRepository;

    @EventHandler
    public void on(InventoryCreatedEvent inventoryCreatedEvent){
        log.info("Projecting InventoryCreatedEvent for productId : {}",
                inventoryCreatedEvent.getProductId());

        InventoryEntity inventory = InventoryEntity.builder()
                .inventoryId(inventoryCreatedEvent.getInventoryId())
                .productId(inventoryCreatedEvent.getProductId())
                .productName(inventoryCreatedEvent.getProductName())
                .availableQuantity(inventoryCreatedEvent.getQuantity())
                .reservedQuantity(0)
                .status(InventoryStatus.AVAILABLE)
                .build();

        inventoryRepository.save(inventory);
        log.info("Inventory saved, inventoryId : {}", inventoryCreatedEvent.getInventoryId());

    }

    @EventHandler
    public void on(InventoryReservedEvent event){
        log.info("Projecting InventoryReservedEvent for orderId : {}",
                event.getOrderId());

        InventoryEntity inventory = getInventory(event.getInventoryId());

        InventoryReservationEntity reservation = InventoryReservationEntity.builder()
                .reservationId(event.getReservationId())
                .inventory(inventory)
                .orderId(event.getOrderId())
                .quantity(event.getQuantity())
                .status(ReservationStatus.RESERVED)
                .build();
        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + event.getQuantity()
        );
        if(inventory.getAvailableQuantity() - inventory.getReservedQuantity() == 0){
            inventory.setStatus(InventoryStatus.DEPLETED);
        }else{
            inventory.setStatus(InventoryStatus.RESERVED);
        }

        inventory.getReservations().add(reservation);
        inventoryRepository.save(inventory);

    }

    @EventHandler
    public void on(InventoryReservationFailedEvent event) {
        // no state change needed — stock was never touched
        log.warn("Inventory reservation failed for orderId: {}, reason: {}",
                event.getOrderId(), event.getReason());
    }

    @EventHandler
    public void on(InventoryReleasedEvent event) {
        log.info("Projecting InventoryReleasedEvent for orderId: {}",
                event.getOrderId());

        InventoryEntity inventory = getInventory(event.getInventoryId());

        // find and update the reservation status
        inventory.getReservations().stream()
                .filter(r -> r.getOrderId().equals(event.getOrderId())
                        && r.getStatus() == ReservationStatus.RESERVED)
                .findFirst()
                .ifPresent(r -> r.setStatus(ReservationStatus.RELEASED));

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - event.getQuantity()
        );
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventoryRepository.save(inventory);
    }

    @EventHandler
    public void on(InventoryReservationConfirmedEvent event) {
        log.info("Projecting InventoryReservationConfirmedEvent for orderId: {}",
                event.getOrderId());

        InventoryEntity inventory = getInventory(event.getInventoryId());

        // find and update the reservation status
        inventory.getReservations().stream()
                .filter(r -> r.getOrderId().equals(event.getOrderId())
                        && r.getStatus() == ReservationStatus.RESERVED)
                .findFirst()
                .ifPresent(r -> r.setStatus(ReservationStatus.CONFIRMED));
        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - event.getQuantity()
        );
        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - event.getQuantity()
        );
        if (inventory.getAvailableQuantity() == 0) {
            inventory.setStatus(InventoryStatus.DEPLETED);
        } else {
            inventory.setStatus(InventoryStatus.AVAILABLE);
        }

        inventoryRepository.save(inventory);
    }





    private InventoryEntity getInventory(String inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalStateException(
                        "Inventory not found, inventoryId: " + inventoryId));
    }
}
