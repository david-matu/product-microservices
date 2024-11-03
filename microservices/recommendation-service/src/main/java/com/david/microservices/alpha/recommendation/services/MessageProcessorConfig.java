package com.david.microservices.alpha.recommendation.services;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.david.microservices.alpha.api.core.recommendation.Recommendation;
import com.david.microservices.alpha.api.core.recommendation.RecommendationService;
import com.david.microservices.alpha.api.event.Event;
import com.david.microservices.alpha.api.exceptions.EventProcessingException;

@Configuration
public class MessageProcessorConfig {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);
	
	private final RecommendationService recommendationService;

	@Autowired
	public MessageProcessorConfig(RecommendationService recommendationService) {
		this.recommendationService = recommendationService;
	}
	
	@Bean
	public Consumer<Event<Integer, Recommendation>> messageProcessor() {
		return event -> {
			LOG.info("Process message created at {}...", event.getEventCreatedAt());
			
			switch (event.getEventType()) {
				case CREATE:
					Recommendation r = event.getData();
					LOG.info("Create recommendation with ID: {}/{}", r.getProductId(), r.getRecommendationId());
					recommendationService.createRecommendation(r).block();
					break;
					
				case DELETE:
					int productId = event.getKey();
					LOG.info("Delete recommendation with ProductID: {}", productId);
					recommendationService.deleteRecommendations(productId).block();
					break;
					
				default:
					String errorMsg = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
					LOG.warn(errorMsg);
					throw new EventProcessingException(errorMsg);
			}
			
			LOG.info("Message processing done");
		};
	}
}
