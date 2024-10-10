package com.david.microservices.alpha.review.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import com.david.microservices.alpha.api.core.review.Review;
import com.david.microservices.alpha.api.core.review.ReviewService;
import com.david.microservices.alpha.api.exceptions.InvalidInputException;
import com.david.microservices.alpha.review.persistence.ReviewEntity;
import com.david.microservices.alpha.review.persistence.ReviewRepository;
import com.david.microservices.alpha.util.http.ServiceUtil;

@RestController
public class ReviewServiceImpl implements ReviewService {

	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
	
	private final ServiceUtil serviceUtil;
	
	private final ReviewMapper mapper;
	private final ReviewRepository repo;
	
	/**
	 * 
	 * @param repo
	 * @param mapper
	 * @param serviceUtil
	 */
	@Autowired
	public ReviewServiceImpl(ReviewRepository repo, ReviewMapper mapper, ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repo = repo;
	}

	@Override
	public List<Review> getReviews(int productId) {
		
		if(productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}
		
		/*
		if(productId == 213) {
			LOG.debug("No review found for productId: {}", productId);
			return new ArrayList<>();
		}
		
		List<Review> list = new ArrayList<>();
		list.add(new Review(productId, 1, "Author 1", "Subject A", "Content A1", serviceUtil.getServiceAddress()));
		list.add(new Review(productId, 2, "Author 2", "Subject B", "Content B1", serviceUtil.getServiceAddress()));
		list.add(new Review(productId, 3, "Author 3", "Subject C", "Content C1", serviceUtil.getServiceAddress()));
		
		LOG.debug("/reviews response size: {}", list.size());
		*/
		
		List<ReviewEntity> entityList = repo.findByProductId(productId);
		List<Review> list = mapper.entityListToApiList(entityList);
		
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
		
		LOG.debug("getReviews: response size: {}", list.size());
		
		return list;
	}

	@Override
	public Review createReview(Review body) {
		try {
			ReviewEntity entity = mapper.apiToEntity(body);
			ReviewEntity newEntity = repo.save(entity);
			
			LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
			return mapper.entityToApi(newEntity);
		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id: " + body.getReviewId());
		}
		
	}

	@Override
	public void deleteReviews(int productId) {
		LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
		repo.deleteAll(repo.findByProductId(productId));
	}
	
}
