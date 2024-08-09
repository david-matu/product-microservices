package com.david.microservices.alpha.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
	
	private static final String PRODUCT_ID_OK = null;
	@Autowired
	private WebTestClient client;
	
	
	@Test
	void contextLoads() {
	}
	
	/*
	@Test
	void getProductById() {
		client.get()
			.uri("/product-composite/" + PRODUCT_ID_OK)
	}
	*/

}
