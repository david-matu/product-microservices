package com.david.microservices.alpha.api.core.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
	Product getProduct(@PathVariable int productId);
}
