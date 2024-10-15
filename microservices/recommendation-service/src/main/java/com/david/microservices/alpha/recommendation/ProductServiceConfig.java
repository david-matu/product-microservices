package com.david.microservices.alpha.recommendation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.client.MongoClient;

//@Configuration
public class ProductServiceConfig {
	
	/*
	@Bean
	public ReactiveMongoOperations reactiveMongoTemplate(MongoClient mongoClient) {
		return new ReactiveMongoTemplate(mongoClient, "products");
	}
	*/
}
