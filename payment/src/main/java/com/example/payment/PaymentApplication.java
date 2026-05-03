package com.example.payment;

import com.example.common.config.AxonConfig;
import com.example.payment.command.interceptor.PaymentCommandInterceptor;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AxonConfig.class)
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

	@Autowired
	public void registerPaymentCommandInterceptor(ApplicationContext context,
												  CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(
				context.getBean(PaymentCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer config) {
		config.registerListenerInvocationErrorHandler("payment-group",
				conf -> PropagatingErrorHandler.instance());
	}
}