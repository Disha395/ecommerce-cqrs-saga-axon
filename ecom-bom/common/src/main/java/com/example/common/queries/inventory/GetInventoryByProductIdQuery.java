package com.example.common.queries.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetInventoryByProductIdQuery {
    private final String productId;
}
