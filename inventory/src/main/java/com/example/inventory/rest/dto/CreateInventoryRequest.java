package com.example.inventory.rest.dto;

import lombok.Data;

@Data
public class CreateInventoryRequest {
    private String productId;
    private String productName;
    private Integer quantity;
}
