package com.david.microservices.alpha.api.core.recommendation;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface RecommendationService {
	
	/**
	 * Example usage: "curl $HOST:$PORT/recommendation?productId=1"
	 * 
	 * @param productId Id of the product
	 * @return the recommendations of the product
	 */
	@GetMapping(value= "/recommendations", produces = "application/json")
	List<Recommendation> getRecommendations(@RequestParam(value = "productId", required = true) int productId);
	
	@PostMapping(value = "/recommendations", consumes = "application/json", produces = "application/json")
	Recommendation createRecommendation(@RequestBody Recommendation body);
	
	@DeleteMapping(value = "/recommendations")
	void deleteRecommendations(@RequestParam(value = "productId", required = true) int productId);
}
