package com.example.payment.query.projection;

import com.example.common.events.payment.PaymentCancelledEvent;
import com.example.common.events.payment.PaymentFailedEvent;
import com.example.common.events.payment.PaymentProcessedEvent;
import com.example.payment.model.entity.PaymentEntity;
import com.example.payment.model.enums.PaymentStatus;
import com.example.payment.query.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ProcessingGroup("payment-group")
public class PaymentProjection {

    private final PaymentRepository paymentRepository;


    @EventHandler
    public void on(PaymentProcessedEvent event) {
        if (paymentRepository.findByOrderId(event.getOrderId()).isPresent()) {
            log.warn("Duplicate payment detected for orderId: {}", event.getOrderId());
            return;
        }

        log.info("Projecting PaymentProcessedEvent for orderId: {}",
                event.getOrderId());

        PaymentEntity payment = PaymentEntity.builder()
                .paymentId(event.getPaymentId())
                .orderId(event.getOrderId())
                .customerId(event.getCustomerId())
                .amount(event.getAmount())
                .status(PaymentStatus.COMPLETED)
                .build();

        paymentRepository.save(payment);
        log.info("Payment saved, paymentId: {}", event.getPaymentId());
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.info("Projecting PaymentFailedEvent for orderId: {}",
                event.getOrderId());

        PaymentEntity payment = PaymentEntity.builder()
                .paymentId(event.getPaymentId())
                .orderId(event.getOrderId())
                .customerId(event.getCustomerId())
                .amount(event.getAmount())
                .status(PaymentStatus.FAILED)
                .failureReason(event.getReason())
                .build();

        paymentRepository.save(payment);
        log.info("Payment failed record saved, paymentId: {}", event.getPaymentId());
    }

    @EventHandler
    public void on(PaymentCancelledEvent event) {
        log.info("Projecting PaymentCancelledEvent for orderId: {}",
                event.getOrderId());

        PaymentEntity payment = getPayment(event.getPaymentId());
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setFailureReason(event.getReason());
        paymentRepository.save(payment);
    }

    private PaymentEntity getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException(
                        "Payment not found, paymentId: " + paymentId));
    }
}