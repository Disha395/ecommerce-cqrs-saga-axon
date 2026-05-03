package com.example.inventory.rest;

import com.example.common.commands.inventory.CreateInventoryCommand;
import com.example.inventory.rest.dto.CreateInventoryRequest;
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
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Commands", description = "Endpoints for managing inventory")
public class InventoryCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    @Operation(summary = "Create inventory for a product")
    public ResponseEntity<String> createInventory(
            @RequestBody CreateInventoryRequest request) {

        String inventoryId = UUID.randomUUID().toString();
        log.info("Dispatching CreateInventoryCommand for productId: {}",
                request.getProductId());

        commandGateway.sendAndWait(
                CreateInventoryCommand.builder()
                        .inventoryId(inventoryId)
                        .productId(request.getProductId())
                        .productName(request.getProductName())
                        .quantity(request.getQuantity())
                        .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryId);
    }
}