package com.example.inventory.query.handler;

import com.example.inventory.model.enity.InventoryEntity;
import com.example.inventory.model.enity.InventoryReservationEntity;
import com.example.inventory.query.api.queries.GetInventoryByIdQuery;
import com.example.common.queries.inventory.GetInventoryByProductIdQuery;
import com.example.inventory.query.api.response.InventoryReservationResponse;
import com.example.inventory.query.api.response.InventoryResponse;
import com.example.inventory.query.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryQueryHandler {

    private final InventoryRepository inventoryRepository;

    @QueryHandler
    public InventoryResponse handle(GetInventoryByIdQuery query) {
        log.info("Handling GetInventoryByIdQuery for inventoryId: {}",
                query.getInventoryId());

        InventoryEntity inventory = inventoryRepository
                .findById(query.getInventoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inventory not found, inventoryId: " + query.getInventoryId()));

        return mapToResponse(inventory);
    }


    // for Saga — returns lightweight common response
    @QueryHandler
    public com.example.common.dto.inventory.InventoryResponse handle(
            GetInventoryByProductIdQuery query) {

        InventoryEntity inventory = inventoryRepository
                .findByProductId(query.getProductId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inventory not found, productId: " + query.getProductId()));

        // map to common lightweight response
        return com.example.common.dto.inventory.InventoryResponse.builder()
                .inventoryId(inventory.getInventoryId())
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .build();
    }

    private InventoryResponse mapToResponse(InventoryEntity inventory) {
        List<InventoryReservationResponse> reservations = inventory.getReservations()
                .stream()
                .map(this::mapReservationToResponse)
                .collect(Collectors.toList());

        return InventoryResponse.builder()
                .inventoryId(inventory.getInventoryId())
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .status(inventory.getStatus())
                .reservations(reservations)
                .build();
    }

    private InventoryReservationResponse mapReservationToResponse(
            InventoryReservationEntity reservation) {
        return InventoryReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .orderId(reservation.getOrderId())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus())
                .build();
    }
}