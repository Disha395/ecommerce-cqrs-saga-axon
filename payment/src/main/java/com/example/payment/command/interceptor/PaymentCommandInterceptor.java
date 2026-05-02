package com.example.payment.command.interceptor;

import com.example.payment.command.api.commands.ProcessPaymentCommand;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
@Component
public class PaymentCommandInterceptor implements
        MessageDispatchInterceptor<CommandMessage<?>> {

    @Override
    @Nonnull
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {

        return (index, command) -> {

            log.info("Intercepted command: {}", command.getPayloadType().getSimpleName());

            if (command.getPayload() instanceof ProcessPaymentCommand processPaymentCommand) {

                // paymentId must not be blank
                if (processPaymentCommand.getPaymentId() == null ||
                        processPaymentCommand.getPaymentId().isBlank()) {
                    throw new IllegalArgumentException("PaymentId must not be blank");
                }

                // orderId must not be blank
                if (processPaymentCommand.getOrderId() == null ||
                        processPaymentCommand.getOrderId().isBlank()) {
                    throw new IllegalArgumentException("OrderId must not be blank");
                }

                // customerId must not be blank
                if (processPaymentCommand.getCustomerId() == null ||
                        processPaymentCommand.getCustomerId().isBlank()) {
                    throw new IllegalArgumentException("CustomerId must not be blank");
                }

                // amount must be positive
                if (processPaymentCommand.getAmount() == null ||
                        processPaymentCommand.getAmount()
                                .compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException(
                            "Amount must be greater than zero"
                    );
                }
            }

            return command;
        };
    }
}