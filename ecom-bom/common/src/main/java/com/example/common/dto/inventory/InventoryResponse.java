// common/dto/inventory/InventoryResponse.java
package com.example.common.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}