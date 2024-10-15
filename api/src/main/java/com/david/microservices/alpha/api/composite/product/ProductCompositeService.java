package com.david.microservices.alpha.api.composite.product;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import reactor.core.publisher.Mono;

public interface ProductCompositeService {
	
	/**
	 * Sample usage: "curl $HOST:$PORT/product-composite/1"
	 * 
	 * @param productId - id of the product
	 * @return the composite product info, if found, else null
	 * 
	 */
	
	@Operation(
			summary = "${api.product-composite.get-composite-product.description}",
			description = "${api.product-composite.get-composite-product.notes}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
			@ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
	})
	@GetMapping(value = "/product-composite/{productId}", produces = "application/json")
	Mono<ProductAggregate> getProduct(@PathVariable int productId);
	
	
	@Operation(summary = "${api.product-composite.create-composite-product.description}", description = "${api.product-composite.create-composite-product.notes}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
	})
	@PostMapping(value = "/product-composite", consumes = "application/json")
	Mono<Void> createProduct(@RequestBody ProductAggregate body);
	
	@Operation(summary = "${api.product-composite.delete-product-composite.description}", description = "${api.product-composite.delete-product-composite.notes}")
	@DeleteMapping(value = "/product-composite/{productId}")
	Mono<Void> deleteProduct(@PathVariable int productId);
	
}
