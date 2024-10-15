package com.david.microservices.alpha.composite.product.services;

import static java.util.logging.Level.FINE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.david.microservices.alpha.api.composite.product.ProductAggregate;
import com.david.microservices.alpha.api.composite.product.ProductCompositeService;
import com.david.microservices.alpha.api.composite.product.RecommendationSummary;
import com.david.microservices.alpha.api.composite.product.ReviewSummary;
import com.david.microservices.alpha.api.composite.product.ServiceAddresses;
import com.david.microservices.alpha.api.core.product.Product;
import com.david.microservices.alpha.api.core.recommendation.Recommendation;
import com.david.microservices.alpha.api.core.review.Review;
import com.david.microservices.alpha.util.http.ServiceUtil;

import reactor.core.publisher.Mono;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
	
	private final ServiceUtil serviceUtil;
	private final ProductCompositeIntegration integration;
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);
	
	@Autowired
	public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	public Mono<ProductAggregate> getProduct(int productId) {
		
		// The requests will be made in parallel and finally zipped together
		LOG.info("Will get composite product info for product.id={}", productId);
		
		
		return Mono.zip(
				values -> createProductAggregate((Product) values[0], (List<Recommendation>) values[1], (List<Review>) values[2], serviceUtil.getServiceAddress()),
				integration.getProduct(productId),
				integration.getRecommendations(productId).collectList(),
				integration.getReviews(productId).collectList())
				.doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
				.log(LOG.getName(), FINE);
	}
	
	
	private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress) {
		
		// 1. Setup product info
		int productId = product.getProductId();
		String name = product.getName();
		int weight = product.getWeight();
		
		// 2. Copy summary recommendation info if available
		List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null : recommendations.stream()
				.map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
				.collect(Collectors.toList());
		
		// 3. Copy summary review info if available
		List<ReviewSummary> reviewSummaries = (reviews == null) ? null : reviews.stream()
				.map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
				.collect(Collectors.toList());
		
		// 4. Create info regarding the involved microservices addresses
		String productAddress = product.getServiceAddress();
		String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
		String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);
		
		return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
	}
	
	
	@Override
	public Mono<Void> createProduct(ProductAggregate body) {
		try {
			List<Mono> monoList = new ArrayList<>();
			
			LOG.info("Will create a new composite entity for product.id: {}", body.getProductId());
			
			Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
			
			monoList.add(integration.createProduct(product));
			
			if(body.getRecommendations() != null) {
				body.getRecommendations().forEach(r -> {
					Recommendation rec = new Recommendation(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
					monoList.add(integration.createRecommendation(rec));
				});
			}
			
			if(body.getReviews() != null) {
				body.getReviews().forEach(r -> {
					Review rev = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
					monoList.add(integration.createReview(rev));
				});
			}
			
			LOG.debug("createComposite: composite entities created for productId: {}", body.getProductId());
			
			return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
					.doOnError(ex -> LOG.warn("createCompositeProduct failed: {}", ex.toString()))
					.then();
		} catch (RuntimeException rex) {
			LOG.warn("createCompositeProduct failed: {}", rex.toString());
			throw rex;
		}
	}

	@Override
	public Mono<Void> deleteProduct(int productId) {
		try {
			LOG.info("Will delete a product aggregate for product.id: {}", productId);
			
			
			return Mono.zip(
					r -> "",
					integration.deleteProduct(productId),
					integration.deleteRecommendations(productId),
					integration.deleteReviews(productId))
					.doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
					.log(LOG.getName(), FINE).then();
		} catch (RuntimeException rex) {
			LOG.warn("deleteCompositeProduct failed: {}", rex.toString());
		}
		return Mono.empty();
	}
}
