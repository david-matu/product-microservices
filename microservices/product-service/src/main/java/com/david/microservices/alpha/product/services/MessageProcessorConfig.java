package com.david.microservices.alpha.product.services;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.david.microservices.alpha.api.core.product.Product;
import com.david.microservices.alpha.api.core.product.ProductService;
import com.david.microservices.alpha.api.event.Event;
import com.david.microservices.alpha.api.exceptions.EventProcessingException;

@Configuration
public class MessageProcessorConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);
	
	private final ProductService productService;

	public MessageProcessorConfig(ProductService productService) {
		this.productService = productService;
	}
	
	@Bean
	public Consumer<Event<Integer, Product>> messageProcessor() {
		return event -> {
			LOG.info("Process message created at {}...", event.getEventCreatedAt());
			
			switch(event.getEventType()) {
				case CREATE:
					Product prod = event.getData();
					
					LOG.info("Create product with ID: {}", prod.getProductId());
					productService.createProduct(prod).block();
					break;
					
				case DELETE:
					int productId = event.getKey();
					LOG.info("Delete product with ProductID: {}", productId);
					productService.deleteProduct(productId).block();
					break;
					
				default:
					String errorMsg = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
					
					LOG.warn(errorMsg);
					throw new EventProcessingException(errorMsg);
			}
			
			LOG.info("Message processing done!");
		};
	}
}