package com.david.microservices.alpha.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.HttpStatus.*;
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.david.microservices.alpha.api.core.review.Review;
import com.david.microservices.alpha.review.persistence.ReviewRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static  org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
// import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ReviewServiceApplicationTests extends MySqlTestBase {
	
	/* writing off these to add persistence tests
	private static int PRODUCT_ID_OK = 1;
	private static int PRODUCT_ID_NOT_FOUND = 213;
	private static int PRODUCT_ID_INVALID = -1;
	*/
	
	@Autowired
	private WebTestClient client;
	
	@Autowired
	private ReviewRepository repo;
	
	@BeforeEach
	void setupDb() {
		repo.deleteAll();
	}
	
	@Test
	void getReviewsByProductId() {
		int productId = 1;
		
		// Acertain that there are zero reviews for any product now that the reviews db has been cleared before test
		assertEquals(0, repo.findByProductId(productId).size());
		
		// Post the reviews against product (1)
		postAndVerifyReview(productId, 1, OK);
		postAndVerifyReview(productId, 2, OK);
		postAndVerifyReview(productId, 3, OK);
		
		// Assert that 3 reviews have been inserted successfully against a productId
		assertEquals(3, repo.findByProductId(productId).size());
		
		getAndVerifyReviewsByProductId(productId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].productId").isEqualTo(productId)
			.jsonPath("$[2].reviewId").isEqualTo(3);
	}
	
	@Test
	void duplicateError() {
		int productId = 1;
		int reviewId = 1;
		
		assertEquals(0, repo.count());
		
		postAndVerifyReview(productId, reviewId, OK)
			.jsonPath("$.productId").isEqualTo(productId)
			.jsonPath("$.reviewId").isEqualTo(reviewId);
		
		assertEquals(1, repo.count());
		
		postAndVerifyReview(productId, reviewId, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/reviews")
			.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id: 1");
		
		assertEquals(1, repo.count());
	}
	
	@Test
	void deleteReviews() {
		int productId = 1;
		int reviewId = 1;
		
		postAndVerifyReview(productId, reviewId, OK);
		assertEquals(1, repo.findByProductId(productId).size());
		
		deleteAndVerifyReviewsByProductId(productId, OK);
		assertEquals(0, repo.findByProductId(productId).size());
		
		deleteAndVerifyReviewsByProductId(productId, OK);
	}
	
	@Test
	void getReviewsMissingParameter() {
		getAndVerifyReviewsByProductId("", BAD_REQUEST)
			.jsonPath("$.path").isEqualTo("/reviews")
			.jsonPath("$.message").isEqualTo("Required query parameter 'productId' is not present.");
	}
	
	@Test
	void getReviewsInvalidParameter() {
		getAndVerifyReviewsByProductId("?productId=non-integer", BAD_REQUEST)
			.jsonPath("$.path").isEqualTo("/reviews")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
			
	}
	
	@Test
	void getReviewsNotFound() {
		getAndVerifyReviewsByProductId("?productId=213", OK)
			.jsonPath("$.length()").isEqualTo(0);
	}
	
	private WebTestClient.BodyContentSpec  deleteAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/reviews?productId=" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/reviews?productId=" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}
	
	// For invalid parameter, string which is not expect. 
	private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(String productIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/reviews" + productIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}
	
	private WebTestClient.BodyContentSpec postAndVerifyReview(int productId, int reviewId, HttpStatus expectedStatus) {
		Review review = new Review(productId, reviewId, "Author " + reviewId, "Subject " + reviewId, "Content" + reviewId, "SA");
		
		return client.post()
				.uri("/reviews")
				.body(just(review), Review.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}
	
	/*
	@Test
	void contextLoads() {}
	
	@Test
	void getReviewsByProductId() {
		
		client.get()
			.uri("/reviews?productId=" + PRODUCT_ID_OK)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].productId").isEqualTo(PRODUCT_ID_OK);
	}
	
	@Test
	void getReviewsMissingParameter() {
		client.get()
			.uri("/reviews")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/reviews")
				.jsonPath("$.message").isEqualTo("Required query parameter 'productId' is not present.");
	}
	
	
	@Test
	void getReviewsInvalidParameter() {
		client.get()
			.uri("/reviews?productId=no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/reviews")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	
	@Test
	void getReviewsNotFound() {
		client.get()
			.uri("/reviews?productId=" + PRODUCT_ID_NOT_FOUND)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}
	
	
	@Test
	void getReviewsInvalidParameterNegativeValue() {
		client.get()
			.uri("/reviews?productId=" + PRODUCT_ID_INVALID)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/reviews")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + PRODUCT_ID_INVALID);
	}
	*/
}
