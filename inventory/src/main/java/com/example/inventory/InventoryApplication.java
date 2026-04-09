package com.example.inventory;

import com.example.inventory.command.interceptor.InventoryCommandInterceptor;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

	@Autowired
	public void registerInventoryCommandInterceptor(ApplicationContext context,
													CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(
				context.getBean(InventoryCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer config) {
		config.registerListenerInvocationErrorHandler("inventory-group",
				conf -> PropagatingErrorHandler.instance());
	}
}