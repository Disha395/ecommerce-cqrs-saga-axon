package com.example.inventory.query.api.queries;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetInventoryByIdQuery {

    private final String inventoryId;
}
