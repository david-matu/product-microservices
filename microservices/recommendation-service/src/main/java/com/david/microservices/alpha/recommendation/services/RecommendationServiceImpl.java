package com.david.microservices.alpha.recommendation.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import com.david.microservices.alpha.api.core.recommendation.Recommendation;
import com.david.microservices.alpha.api.core.recommendation.RecommendationService;
import com.david.microservices.alpha.api.exceptions.InvalidInputException;
import com.david.microservices.alpha.recommendation.persistence.RecommendationEntity;
import com.david.microservices.alpha.recommendation.persistence.RecommendationRepository;
import com.david.microservices.alpha.util.http.ServiceUtil;

@RestController
public class RecommendationServiceImpl implements RecommendationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);
	
	private final ServiceUtil serviceUtil;
	
	private final RecommendationRepository repo;
	private final RecommendationMapper mapper;
	
	@Autowired
	public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
		this.repo = repository;
		this.mapper = mapper;
	}

	@Override
	public List<Recommendation> getRecommendations(int productId) {
		
		if(productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}
		
		if(productId == 113) {
			LOG.debug("No recommendations found for productId: {}", productId);
			return new ArrayList<>();
		}
		
		List<Recommendation> list = new ArrayList<>();
		list.add(new Recommendation(productId, 1, "Author 1", 1, "Content R1", serviceUtil.getServiceAddress()));
		list.add(new Recommendation(productId, 2, "Author 2", 2, "Content R2", serviceUtil.getServiceAddress()));
		list.add(new Recommendation(productId, 3, "Author 3", 3, "Content R3", serviceUtil.getServiceAddress()));
		
		LOG.debug("/recommendations response size: {}", list.size());
		
		return list;
	}

	@Override
	public Recommendation createRecommendation(Recommendation body) {
		try {
			RecommendationEntity entity = mapper.apiToEntity(body);
			RecommendationEntity newEntity = repo.save(entity);
			
			LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
			return mapper.entityToApi(newEntity);
		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id: " + body.getRecommendationId());
		}
	}

	@Override
	public void deleteRecommendations(int productId) {
		LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
		repo.deleteAll(repo.findByProductId(productId));
	}

}
