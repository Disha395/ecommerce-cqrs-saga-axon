package com.example.payment.rest;

import com.example.common.commands.payment.CancelPaymentCommand;
import com.example.common.commands.payment.ProcessPaymentCommand;
import com.example.payment.rest.dto.ProcessPaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Commands", description = "Endpoints for processing payments")
public class PaymentCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    @Operation(summary = "Process a payment")
    public ResponseEntity<String> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {

        String paymentId = UUID.randomUUID().toString();
        log.info("Dispatching ProcessPaymentCommand for orderId: {}",
                request.getOrderId());

        commandGateway.sendAndWait(
                ProcessPaymentCommand.builder()
                        .paymentId(paymentId)
                        .orderId(request.getOrderId())
                        .customerId(request.getCustomerId())
                        .amount(request.getAmount())
                        .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentId);
    }

    @DeleteMapping("/{paymentId}")
    @Operation(summary = "Cancel a payment")
    public ResponseEntity<String> cancelPayment(
            @PathVariable("paymentId") String paymentId,
            @RequestParam("reason") String reason) {

        log.info("Dispatching CancelPaymentCommand for paymentId: {}", paymentId);

        commandGateway.sendAndWait(
                new CancelPaymentCommand(paymentId, null, reason)
        );

        return ResponseEntity.ok("Payment cancelled successfully");
    }
}