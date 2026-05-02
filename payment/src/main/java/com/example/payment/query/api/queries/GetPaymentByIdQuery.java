package com.example.payment.query.api.queries;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetPaymentByIdQuery {
    private final String paymentId;
}