package com.example.payment.rest.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProcessPaymentRequest {
    private String orderId;
    private String customerId;
    private BigDecimal amount;
}