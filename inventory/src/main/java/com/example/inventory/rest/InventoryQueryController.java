// rest/InventoryQueryController.java
package com.example.inventory.rest;

import com.example.inventory.query.api.queries.GetInventoryByIdQuery;
import com.example.inventory.query.api.queries.GetInventoryByProductIdQuery;
import com.example.inventory.query.api.response.InventoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Queries", description = "Endpoints for querying inventory")
public class InventoryQueryController {

    private final QueryGateway queryGateway;

    @GetMapping("/{inventoryId}")
    @Operation(summary = "Get inventory by ID")
    public CompletableFuture<InventoryResponse> getInventoryById(
            @PathVariable("inventoryId") String inventoryId) {
        log.info("Dispatching GetInventoryByIdQuery for inventoryId: {}", inventoryId);
        return queryGateway.query(
                new GetInventoryByIdQuery(inventoryId),
                InventoryResponse.class
        );
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory by product ID")
    public CompletableFuture<InventoryResponse> getInventoryByProductId(
            @PathVariable("productId") String productId) {
        log.info("Dispatching GetInventoryByProductIdQuery for productId: {}", productId);
        return queryGateway.query(
                new GetInventoryByProductIdQuery(productId),
                InventoryResponse.class
        );
    }
}