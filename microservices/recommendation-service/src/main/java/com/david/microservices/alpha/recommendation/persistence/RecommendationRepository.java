package com.david.microservices.alpha.recommendation.persistence;

// import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface RecommendationRepository extends ReactiveCrudRepository<RecommendationEntity, String>{
	
	Flux<RecommendationEntity> findByProductId(int productId);
}

// import org.springframework.data.repository.CrudRepository;

/*
public interface RecommendationRepository extends CrudRepository<RecommendationEntity, String>{
	
	List<RecommendationEntity> findByProductId(int productId);
}
*/