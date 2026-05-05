package com.example.order;

import com.example.common.config.AxonConfig;
import com.example.common.exception.GlobalExceptionHandler;
import com.example.order.command.interceptor.OrderCommandInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AxonConfig.class, GlobalExceptionHandler.class})
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

	@Autowired
	public void registerOrderCommandInterceptor(ApplicationContext context,
												CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(
				context.getBean(OrderCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer config) {
		config.registerListenerInvocationErrorHandler("order-group",
				conf -> PropagatingErrorHandler.instance());

		config.usingTrackingEventProcessors();

	}

}
