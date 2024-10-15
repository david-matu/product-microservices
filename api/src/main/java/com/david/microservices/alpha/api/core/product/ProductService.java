package com.david.microservices.alpha.api.core.product;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import reactor.core.publisher.Mono;

/**
 * Expose getProduct() API method, which will be extended
 * 
 */
public interface ProductService {
	
	/**
	 * Usage: "curl $HOST:$PORT/product/1
	 * 
	 * @param productId - id of the product
	 * @return the product if found, else null
	 */
	@GetMapping(value = "/product/{productId}", produces = "application/json")
	Mono<Product> getProduct(@PathVariable int productId);
	
	/*
	@GetMapping(value = "/product/{productId}", produces = "application/json")
	Product getProduct(@PathVariable int productId);
	*/
	@PostMapping(value = "/product", consumes = "application/json", produces = "application/json")
	Mono<Product> createProduct(@RequestBody Product body);
	
	@DeleteMapping(value = "/product/{productid}")
	Mono<Void> deleteProduct(@PathVariable int productId);
}
