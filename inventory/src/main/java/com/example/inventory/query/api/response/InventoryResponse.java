package com.example.inventory.query.api.response;

import com.example.inventory.model.enums.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private String inventoryId;
    private String productId;
    private String productName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private InventoryStatus status;
    private List<InventoryReservationResponse> reservations;

}
