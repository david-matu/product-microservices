package com.david.microservices.alpha.recommendation;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RecommendationServiceApplicationTests {

	private static int PRODUCT_ID_OK = 1;
	private static int PRODUCT_ID_NOT_FOUND = 113;
	private static int PRODUCT_ID_INVALID = -1;
	
	
	@Autowired
	private WebTestClient client;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	void getRecommendationsByProductId() {
		
		client.get()
			.uri("/recommendations?productId=" + PRODUCT_ID_OK)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OK);
	}
	
	@Test
	void getRecommendationsMissingParameter() {
		client.get()
			.uri("/recommendations")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendations")
				.jsonPath("$.message").isEqualTo("Required query parameter 'productId' is not present.");
	}
	
	
	@Test
	void getRecommendationsInvalidParameter() {
		client.get()
			.uri("/recommendations?productId=no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendations")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	
	@Test
	void getRecommendationsNotFound() {
		client.get()
			.uri("/recommendations?productId=" + PRODUCT_ID_NOT_FOUND)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}
	
	
	@Test
	void getRecommendationsInvalidParameterNegativeValue() {
		client.get()
			.uri("/recommendations?productId=" + PRODUCT_ID_INVALID)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/recommendations")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + PRODUCT_ID_INVALID);
	}

}
