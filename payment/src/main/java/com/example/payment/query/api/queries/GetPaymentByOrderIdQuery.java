package com.example.payment.query.api.queries;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetPaymentByOrderIdQuery {
    private final String orderId;
}