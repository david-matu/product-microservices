package com.david.microservices.alpha.review.services;

import java.util.List;

import static java.util.logging.Level.FINE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import com.david.microservices.alpha.api.core.review.Review;
import com.david.microservices.alpha.api.core.review.ReviewService;
import com.david.microservices.alpha.api.exceptions.InvalidInputException;
import com.david.microservices.alpha.review.persistence.ReviewEntity;
import com.david.microservices.alpha.review.persistence.ReviewRepository;
import com.david.microservices.alpha.util.http.ServiceUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
public class ReviewServiceImpl implements ReviewService {

	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
	
	private final ServiceUtil serviceUtil;
	
	private final ReviewMapper mapper;
	private final ReviewRepository repo;
	
	private final Scheduler jdbcScheduler;
	
	/**
	 * 
	 * @param repo
	 * @param mapper
	 * @param serviceUtil
	 */
	@Autowired
	public ReviewServiceImpl(@Qualifier("jdbcScheduler") Scheduler jdbcScheduler, ReviewRepository repo, ReviewMapper mapper, ServiceUtil serviceUtil) {
		this.jdbcScheduler = jdbcScheduler;
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repo = repo;
	}

	@Override
	public Flux<Review> getReviews(int productId) {
		
		if(productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}
		
		LOG.info("Will get revies for product with id={}", productId);
		
		return Mono.fromCallable(() -> internalGetReviews(productId))
				.flatMapMany(Flux::fromIterable)
				.log(LOG.getName(), FINE)
				.subscribeOn(jdbcScheduler);
	}

	private List<Review> internalGetReviews(int productId) {
		List<ReviewEntity> entityList = repo.findByProductId(productId);
		List<Review> list = mapper.entityListToApiList(entityList);
		
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
		
		LOG.debug("Response size: {}", list.size());
		
		return list;
	}

	@Override
	public Mono<Review> createReview(Review body) {
		/*
		
		*/
		
		if(body.getProductId() < 1) {
			throw new InvalidInputException("Invalid productId: " + body.getProductId());
		}
		
		return Mono.fromCallable(() -> internalCreateReview(body))
				.subscribeOn(jdbcScheduler);
	}
	
	private Review internalCreateReview(Review body) {
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
	public Mono<Void> deleteReviews(int productId) {
		if(productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}
		
		return Mono.fromRunnable(() -> internalDeleteReviews(productId)).subscribeOn(jdbcScheduler).then();
	}

	private void internalDeleteReviews(int productId) {
		LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
		
		repo.deleteAll(repo.findByProductId(productId));
	}
}
