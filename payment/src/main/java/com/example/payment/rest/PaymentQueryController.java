package com.example.payment.rest;

import com.example.payment.query.api.queries.GetPaymentByIdQuery;
import com.example.payment.query.api.queries.GetPaymentByOrderIdQuery;
import com.example.payment.query.api.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Queries", description = "Endpoints for querying payments")
public class PaymentQueryController {

    private final QueryGateway queryGateway;

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID")
    public CompletableFuture<PaymentResponse> getPaymentById(
            @PathVariable("paymentId") String paymentId) {
        log.info("Dispatching GetPaymentByIdQuery for paymentId: {}", paymentId);
        return queryGateway.query(
                new GetPaymentByIdQuery(paymentId),
                PaymentResponse.class
        );
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    public CompletableFuture<PaymentResponse> getPaymentByOrderId(
            @PathVariable("orderId") String orderId) {
        log.info("Dispatching GetPaymentByOrderIdQuery for orderId: {}", orderId);
        return queryGateway.query(
                new GetPaymentByOrderIdQuery(orderId),
                PaymentResponse.class
        );
    }
}