package com.example.order.command.interceptor;

import com.example.order.command.api.commands.CreateOrderCommand;
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
public class OrderCommandInterceptor implements
        MessageDispatchInterceptor<CommandMessage<?>> {

    @Override
    @Nonnull
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {

        return (index, command) -> {

            log.info("Intercepted command: {}", command.getPayloadType().getSimpleName());

            if (command.getPayload() instanceof CreateOrderCommand createOrderCommand) {

                // orderId must not be blank
                if (createOrderCommand.getOrderId() == null ||
                        createOrderCommand.getOrderId().isBlank()) {
                    throw new IllegalArgumentException("OrderId must not be blank");
                }

                // customerId must not be blank
                if (createOrderCommand.getCustomerId() == null ||
                        createOrderCommand.getCustomerId().isBlank()) {
                    throw new IllegalArgumentException("CustomerId must not be blank");
                }

                // shippingAddress must not be blank
                if (createOrderCommand.getShippingAddress() == null ||
                        createOrderCommand.getShippingAddress().isBlank()) {
                    throw new IllegalArgumentException("Shipping address must not be blank");
                }

                // items must not be empty
                if (createOrderCommand.getItems() == null ||
                        createOrderCommand.getItems().isEmpty()) {
                    throw new IllegalArgumentException("Order must have at least one item");
                }

                // totalAmount must be positive
                if (createOrderCommand.getTotalAmount() == null ||
                        createOrderCommand.getTotalAmount()
                                .compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException(
                            "Total amount must be greater than zero"
                    );
                }
            }

            return command;
        };
    }
}
