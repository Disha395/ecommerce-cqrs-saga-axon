package com.example.inventory.query.api.response;

import com.example.inventory.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryReservationResponse {
    private String reservationId;
    private String orderId;
    private Integer quantity;
    private ReservationStatus status;
}
