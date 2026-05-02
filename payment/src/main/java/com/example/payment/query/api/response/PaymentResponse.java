package com.example.payment.query.api.response;

import com.example.payment.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;
}