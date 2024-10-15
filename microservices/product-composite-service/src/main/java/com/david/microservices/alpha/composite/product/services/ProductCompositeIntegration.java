package com.david.microservices.alpha.composite.product.services;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;
import static com.david.microservices.alpha.api.event.Event.Type.CREATE;
import static com.david.microservices.alpha.api.event.Event.Type.DELETE;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.david.microservices.alpha.api.core.product.Product;
import com.david.microservices.alpha.api.core.product.ProductService;
import com.david.microservices.alpha.api.core.recommendation.Recommendation;
import com.david.microservices.alpha.api.core.recommendation.RecommendationService;
import com.david.microservices.alpha.api.core.review.Review;
import com.david.microservices.alpha.api.core.review.ReviewService;
import com.david.microservices.alpha.api.event.Event;
import com.david.microservices.alpha.api.exceptions.InvalidInputException;
import com.david.microservices.alpha.api.exceptions.NotFoundException;
import com.david.microservices.alpha.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {
	
	private final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);
	
	private final ObjectMapper mapper;
	
	private final String productServiceUrl;
	private final String recommendationServiceUrl;
	private final String reviewServiceUrl;
	
	private final WebClient webClient;
	
	private final StreamBridge streamBridge;
	private final Scheduler publishEventScheduler;
	
	@Autowired
	public ProductCompositeIntegration(
			@Qualifier("publishEventScheduler") Scheduler publishEventScheduler,
			WebClient.Builder webClient, 
			ObjectMapper mapper,
			StreamBridge streamBridge,
			@Value("${app.product-service.host}") 
			String productServiceHost, 
			
			@Value("${app.product-service.port}") 
			String productServicePort,
			
			@Value("${app.recommendation-service.host}") 
			String recommendationServiceHost, 
			
			@Value("${app.recommendation-service.port}") 
			String recommendationServicePort,
			
			@Value("${app.review-service.host}") 
			String reviewServiceHost, 
			
			@Value("${app.review-service.port}") 
			String reviewServicePort) {
		
		this.publishEventScheduler = publishEventScheduler;
		this.webClient = webClient.build();
		this.mapper = mapper;
		this.streamBridge = streamBridge;
		
		this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product";
		this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendations?productId=";
		this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/reviews?productId=";
	}

	@Override
	public Mono<Product> getProduct(int productId) {
		
		String url = productServiceUrl + "/" + productId;
		
		LOG.debug("Will call getProduct API on URL: {}", url);
		return webClient.get().uri(url).retrieve().bodyToMono(Product.class)
				.log(LOG.getName(), FINE)
				.onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}
	
	private Throwable handleException(Throwable ex) {
		if(!(ex instanceof WebClientResponseException)) {
			LOG.warn("Got unexpected error: {}, will rethrow it", ex.toString());
			return ex;
		}
		
		WebClientResponseException wbex = (WebClientResponseException)ex;
		
		switch (HttpStatus.resolve(wbex.getStatusCode().value())) {
			case NOT_FOUND:
				return new NotFoundException(getErrorMessage(wbex));
				
			case UNPROCESSABLE_ENTITY:
				return new InvalidInputException(getErrorMessage(wbex));
				
			default:
				LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wbex.getStatusCode());
				LOG.warn("Error body: {}", wbex.getResponseBodyAsString());
				return ex;
		}
	}

	@Override
	public Mono<Product> createProduct(Product body) {
		/*
		try {
			String url = productServiceUrl;
			LOG.debug("Will post a new product to URL: {}", url);
			
			Product product = restTemplate.postForObject(url, body, Product.class);
			LOG.debug("Created a product with id: {}", product.getProductId());
			
			return product;
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
		*/
		
		/*
		return Mono.fromCallable(() -> {
			// Send message
			return body;
		}).subscribeOn(publishEventScheduler);
		*/
		
		return Mono.fromCallable(() -> {
			sendMessage("products-out-0", new Event(CREATE, body.getProductId(), body));
			return body;
		});		
	}

	/**
	 * 
	 * @param bindingName
	 * @param event
	 */
	private void sendMessage(String bindingName, Event event) {
		LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
		
		Message message = MessageBuilder.withPayload(event)
				.setHeader("partitionKey", event.getKey())
				.build();
		
		streamBridge.send(bindingName, message);
	}
	
	@Override
	public Mono<Void> deleteProduct(int productId) {
		/*
		try {
			String url = productServiceUrl + "/" + productId;
			LOG.debug("Will call the deleteProduct API on URL: {}", url);
			
			restTemplate.delete(url);
			
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
		*/
		
		// return Mono.fromRunnable(() -> sendMessage())
		
		return Mono.never();
	}
	
	@Override
	public Flux<Recommendation> getRecommendations(int productId) {
		String url = recommendationServiceUrl + productId;
		
		LOG.debug("Will call getRecommendations API on URL: {}", url);
		
		// If something goes wrong when fetching recommendations, return a partial response
		return webClient.get().uri(url)
				.retrieve().bodyToFlux(Recommendation.class)
				.log(LOG.getName(), FINE)
				.onErrorResume(error -> empty());
	}
	
	
	@Override
	public Flux<Review> getReviews(int productId) {
		String url = reviewServiceUrl + productId;
		
		return webClient.get().uri(url).retrieve().bodyToFlux(Review.class)
				.log(LOG.getName(), FINE)
				.onErrorResume(error -> empty());
	}
	
	private String getErrorMessage(WebClientResponseException wbex) {
		try {
			return mapper.readValue(wbex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return wbex.getMessage();
		}
	}

	@Override
	public Mono<Review> createReview(Review body) {
		return Mono.fromCallable(() -> {
			sendMessage("reviews-out-0", new Event(CREATE, body.getProductId(), body));
			return body;
		}).subscribeOn(publishEventScheduler);
	}

	@Override
	public Mono<Void> deleteReviews(int productId) {		
		return Mono.fromRunnable(() -> sendMessage("reviews-out-0", new Event(DELETE, productId, null)))
				.subscribeOn(publishEventScheduler).then();
	}

	@Override
	public Mono<Recommendation> createRecommendation(Recommendation body) {
		return Mono.fromCallable(() -> {
			sendMessage("recommendations-out-0", new Event(CREATE, body.getProductId(), body));
			return body;
		}).subscribeOn(publishEventScheduler);
	}

	@Override
	public Mono<Void> deleteRecommendations(int productId) {
		return Mono.fromRunnable(() -> sendMessage("recommendations-out-0", new Event(DELETE, productId, null)))
				.subscribeOn(publishEventScheduler).then();
	}
}