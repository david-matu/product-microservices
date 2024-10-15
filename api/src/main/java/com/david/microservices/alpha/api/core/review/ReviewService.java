package com.david.microservices.alpha.api.core.review;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {
	
	/**
	 * Example usage: "curl $HOST:$PORT/review?productId=1"
	 * 
	 * @param productId Id of the product
	 * @return the reviews of the product
	 */
	@GetMapping(value= "/reviews", produces = "application/json")
	Flux<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);
	
	@PostMapping(value = "/reviews", consumes = "application/json", produces = "application/json")
	Mono<Review> createReview(@RequestBody Review body);
	
	@DeleteMapping(value = "/reviews")
	Mono<Void> deleteReviews(@RequestParam(value = "productId", required = true) int productId);
}
