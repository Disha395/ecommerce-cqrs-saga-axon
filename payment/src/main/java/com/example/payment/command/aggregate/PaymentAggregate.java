package com.example.payment.command.aggregate;

import com.example.payment.command.api.commands.CancelPaymentCommand;
import com.example.payment.command.api.commands.ProcessPaymentCommand;
import com.example.common.events.payment.PaymentCancelledEvent;
import com.example.common.events.payment.PaymentFailedEvent;
import com.example.common.events.payment.PaymentProcessedEvent;
import com.example.payment.model.enums.PaymentStatus;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
@Slf4j
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private PaymentStatus status;

//    Command Handlers
    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand command) {
        log.info("Handling ProcessPaymentCommand for orderId: {}",
                command.getOrderId());

        // validate
        if (command.getAmount() == null ||
                command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // simulate payment processing
        // in real world this would call a payment gateway (Stripe, Razorpay etc)
        boolean paymentSuccessful = simulatePayment(command.getAmount());

        if (!paymentSuccessful) {
            AggregateLifecycle.apply(
                    PaymentFailedEvent.builder()
                            .paymentId(command.getPaymentId())
                            .orderId(command.getOrderId())
                            .customerId(command.getCustomerId())
                            .amount(command.getAmount())
                            .reason("Payment declined by payment gateway")
                            .build()
            );
            return;
        }

        AggregateLifecycle.apply(
                PaymentProcessedEvent.builder()
                        .paymentId(command.getPaymentId())
                        .orderId(command.getOrderId())
                        .customerId(command.getCustomerId())
                        .amount(command.getAmount())
                        .build()
        );
    }

    @CommandHandler
    public void handle(CancelPaymentCommand command) {
        log.info("Handling CancelPaymentCommand for orderId: {}",
                command.getOrderId());

        if (this.status == PaymentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Payment already cancelled for orderId: " + command.getOrderId()
            );
        }

        if (this.status == PaymentStatus.REFUNDED) {
            throw new IllegalStateException(
                    "Payment already refunded for orderId: " + command.getOrderId()
            );
        }

        AggregateLifecycle.apply(
                PaymentCancelledEvent.builder()
                        .paymentId(command.getPaymentId())
                        .orderId(command.getOrderId())
                        .reason(command.getReason())
                        .build()
        );
    }

//    Event Sourcing Handlers
    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getAmount();
        this.status = PaymentStatus.COMPLETED;
        log.info("PaymentProcessedEvent sourced, paymentId: {}", this.paymentId);
    }

    @EventSourcingHandler
    public void on(PaymentFailedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getAmount();
        this.status = PaymentStatus.FAILED;
        log.info("PaymentFailedEvent sourced, paymentId: {}", this.paymentId);
    }

    @EventSourcingHandler
    public void on(PaymentCancelledEvent event) {
        this.status = PaymentStatus.CANCELLED;
        log.info("PaymentCancelledEvent sourced, paymentId: {}", this.paymentId);
    }

    //Payment Simulation

    private boolean simulatePayment(BigDecimal amount) {
        // simulate 80% success rate
        // dummy payment integration
        return Math.random() > 0.2;
    }
}