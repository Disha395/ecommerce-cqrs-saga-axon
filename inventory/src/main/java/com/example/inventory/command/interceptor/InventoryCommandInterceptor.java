package com.example.inventory.command.interceptor;

import com.example.common.commands.inventory.CreateInventoryCommand;
import com.example.common.commands.inventory.ReserveInventoryCommand;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
@Component
public class InventoryCommandInterceptor implements
        MessageDispatchInterceptor<CommandMessage<?>> {

    @Override
    @Nonnull
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {

        return (index, command) -> {

            log.info("Intercepted command: {}", command.getPayloadType().getSimpleName());

            if (command.getPayload() instanceof CreateInventoryCommand createInventoryCommand) {

                // inventoryId must not be blank
                if (createInventoryCommand.getInventoryId() == null ||
                        createInventoryCommand.getInventoryId().isBlank()) {
                    throw new IllegalArgumentException("InventoryId must not be blank");
                }

                // productId must not be blank
                if (createInventoryCommand.getProductId() == null ||
                        createInventoryCommand.getProductId().isBlank()) {
                    throw new IllegalArgumentException("ProductId must not be blank");
                }

                // productName must not be blank
                if (createInventoryCommand.getProductName() == null ||
                        createInventoryCommand.getProductName().isBlank()) {
                    throw new IllegalArgumentException("ProductName must not be blank");
                }

                // quantity must be positive
                if (createInventoryCommand.getQuantity() == null ||
                        createInventoryCommand.getQuantity() <= 0) {
                    throw new IllegalArgumentException(
                            "Quantity must be greater than zero"
                    );
                }
            }

            if (command.getPayload() instanceof ReserveInventoryCommand reserveInventoryCommand) {

                // orderId must not be blank
                if (reserveInventoryCommand.getOrderId() == null ||
                        reserveInventoryCommand.getOrderId().isBlank()) {
                    throw new IllegalArgumentException("OrderId must not be blank");
                }

                // productId must not be blank
                if (reserveInventoryCommand.getProductId() == null ||
                        reserveInventoryCommand.getProductId().isBlank()) {
                    throw new IllegalArgumentException("ProductId must not be blank");
                }

                // quantity must be positive
                if (reserveInventoryCommand.getQuantity() == null ||
                        reserveInventoryCommand.getQuantity() <= 0) {
                    throw new IllegalArgumentException(
                            "Quantity must be greater than zero"
                    );
                }
            }

            return command;
        };
    }
}