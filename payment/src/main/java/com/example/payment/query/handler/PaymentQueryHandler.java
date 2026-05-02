package com.example.payment.query.handler;

import com.example.payment.model.entity.PaymentEntity;
import com.example.payment.query.api.queries.GetPaymentByIdQuery;
import com.example.payment.query.api.queries.GetPaymentByOrderIdQuery;
import com.example.payment.query.api.response.PaymentResponse;
import com.example.payment.query.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentQueryHandler {

    private final PaymentRepository paymentRepository;

    @QueryHandler
    public PaymentResponse handle(GetPaymentByIdQuery query) {
        log.info("Handling GetPaymentByIdQuery for paymentId: {}",
                query.getPaymentId());

        PaymentEntity payment = paymentRepository
                .findById(query.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found, paymentId: " + query.getPaymentId()));

        return mapToResponse(payment);
    }

    @QueryHandler
    public PaymentResponse handle(GetPaymentByOrderIdQuery query) {
        log.info("Handling GetPaymentByOrderIdQuery for orderId: {}",
                query.getOrderId());

        PaymentEntity payment = paymentRepository
                .findByOrderId(query.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment not found, orderId: " + query.getOrderId()));

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(PaymentEntity payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}