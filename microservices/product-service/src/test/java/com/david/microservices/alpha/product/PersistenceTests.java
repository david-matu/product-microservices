package com.david.microservices.alpha.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.david.microservices.alpha.product.persistence.ProductEntity;
import com.david.microservices.alpha.product.persistence.ProductRepository;

import reactor.test.StepVerifier;

@DataMongoTest
public class PersistenceTests extends MongoDbTestBase {
	
	@Autowired
	private ProductRepository repo;
	
	private ProductEntity savedEntity;
	
	@BeforeEach
	void setupDb() {
		StepVerifier.create(repo.deleteAll()).verifyComplete();
		
		ProductEntity entity = new ProductEntity(1, "n", 1);
		
		StepVerifier.create(repo.save(entity))
			.expectNextMatches(createdEntity -> {
				System.out.println("Entity created: " + createdEntity.toString());
				savedEntity = createdEntity;
				return areProductEqual(entity, savedEntity);
			})
			.verifyComplete();
		
	}
	
	@Test
	void create() {
		ProductEntity newEntity = new ProductEntity(2, "n", 2);
		
		StepVerifier.create(repo.save(newEntity))
			.expectNextMatches(createdEntity -> {
				//System.out.println("Test 1: Created entity -> " + createdEntity 
					//	+ "\nCreated entity id is same as source object's? " + (newEntity.getProductId() == createdEntity.getProductId()));
				return newEntity.getProductId() == createdEntity.getProductId();
			})
			.verifyComplete();
		
		System.out.println("Product Entities id: " + newEntity.getId());
		
		StepVerifier.create(repo.findById(newEntity.getId()))
			.expectNextMatches(foundEntity -> {
				System.out.println("Product Entities id: " + foundEntity.getId());
				System.out.println("Products are equal: " + areProductEqual(newEntity, foundEntity));
				return areProductEqual(newEntity, foundEntity);
			})
			.verifyComplete();
		
		// StepVerifier.create(repo.count()).expectNext(2L).verifyComplete();
	}

	private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
		//System.out.println("Expected Entity: " + expectedEntity);
		//System.out.println("Actual Entity: " + actualEntity);
		//System.out.println("Ids are equal: " + (expectedEntity.getId().equals(actualEntity.getId())));
		
		boolean areEqual = (expectedEntity.getId().equals(actualEntity.getId()))
				&& (expectedEntity.getVersion() == actualEntity.getVersion())
				&& (expectedEntity.getProductId() == actualEntity.getProductId())
				&& (expectedEntity.getName().equals(actualEntity.getName()))
				&& (expectedEntity.getWeight() == actualEntity.getWeight());
		
		//System.out.println("Product entities are equal?: " + areEqual);
		
		return areEqual;
	}
}
