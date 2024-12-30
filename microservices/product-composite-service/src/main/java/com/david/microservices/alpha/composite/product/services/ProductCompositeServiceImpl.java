package com.david.microservices.alpha.composite.product.services;

import static java.util.logging.Level.FINE;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import com.david.microservices.alpha.api.composite.product.ProductAggregate;
import com.david.microservices.alpha.api.composite.product.ProductCompositeService;
import com.david.microservices.alpha.api.composite.product.RecommendationSummary;
import com.david.microservices.alpha.api.composite.product.ReviewSummary;
import com.david.microservices.alpha.api.composite.product.ServiceAddresses;
import com.david.microservices.alpha.api.core.product.Product;
import com.david.microservices.alpha.api.core.recommendation.Recommendation;
import com.david.microservices.alpha.api.core.review.Review;
import com.david.microservices.alpha.composite.product.services.tracing.ObservationUtil;
import com.david.microservices.alpha.util.http.ServiceUtil;

import reactor.core.publisher.Mono;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
	
	private final ServiceUtil serviceUtil;
	private final ProductCompositeIntegration integration;
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);
	
	private final SecurityContext nullSecCtx = new SecurityContextImpl();
	
	private final ObservationUtil observationUtil;
	
	@Autowired
	public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration, ObservationUtil observationUtil) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
		this.observationUtil = observationUtil;
	}

	// public Mono<ProductAggregate> getProduct(int productId) {
	/*
	 * Make this possible to call with observation, for sake of tracing with Span IDs
	@Override
	public Mono<ProductAggregate> getProduct(int productId, int delay, int faultPercent) {
		
		// The requests will be made in parallel and finally zipped together
		LOG.info("Will get composite product info for product.id={}", productId);
		
		
		return Mono.zip(
				values -> createProductAggregate((SecurityContext) values[0], (Product) values[1], (List<Recommendation>) values[2], (List<Review>) values[3], serviceUtil.getServiceAddress()),
				getSecurityContextMono(),
				integration.getProduct(productId, delay, faultPercent),
				integration.getRecommendations(productId).collectList(),
				integration.getReviews(productId).collectList())
				.doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
				.log(LOG.getName(), FINE);
	}
	*/
	
	@Override
	public Mono<ProductAggregate> getProduct(int productId, int delay, int faultPercent) {
		return observationWithProductInfo(productId, () -> getProductInternal(productId, delay, faultPercent));
	}
	
	public Mono<ProductAggregate> getProductInternal(int productId, int delay, int faultPercent) {
		LOG.info("Will get composite product info for product.id={}", productId);
		
		return Mono.zip(
				values -> createProductAggregate((SecurityContext) values[0], (Product) values[1], (List<Recommendation>) values[2], (List<Review>) values[3], serviceUtil.getServiceAddress()),
				getSecurityContextMono(),
				integration.getProduct(productId, delay, faultPercent),
				integration.getRecommendations(productId).collectList(),
				integration.getReviews(productId).collectList())
				.doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
				.log(LOG.getName(), FINE);
	}
	
	private ProductAggregate createProductAggregate(SecurityContext sc, Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress) {
		
		logAuthorizationInfo(sc);
		
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
	
	// return observationWithProductInfo(productId, () -> getProductInternal(productId, delay, faultPercent));
	
	@Override
	public Mono<Void> createProduct(ProductAggregate body) {
		return observationWithProductInfo(body.getProductId(), () -> createProductInternal(body));
	}
	
	public Mono<Void> createProductInternal(ProductAggregate body) {
		try {
			List<Mono> monoList = new ArrayList<>();
			
			monoList.add(getLogAuthorizationInfoMono());
			
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
		return observationWithProductInfo(productId, () -> deleteProductInternal(productId));
	}
	
	public Mono<Void> deleteProductInternal(int productId) {
		try {
			LOG.info("Will delete a product aggregate for product.id: {}", productId);
			
			return Mono.zip(
					r -> "",
					getLogAuthorizationInfoMono(),
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
	
	// Dec 3, 2024
	
	private Mono<SecurityContext> getLogAuthorizationInfoMono(){
		return getSecurityContextMono().doOnNext(sc -> logAuthorizationInfo(sc));
	}
	
	private Mono<SecurityContext> getSecurityContextMono(){
		return ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSecCtx);
	}
	
	private void logAuthorizationInfo(SecurityContext sc) {
		if (sc != null && sc.getAuthentication() != null && sc.getAuthentication() instanceof JwtAuthenticationToken) {
			Jwt jwtToken = ((JwtAuthenticationToken)sc.getAuthentication()).getToken();
			
			logAuthorizationInfo(jwtToken);
		} else {
			LOG.warn("No JWT based authentication supplied, running tests are we?");
		}
	}
	
	// Dec 3, 2024
	private void logAuthorizationInfo(Jwt jwt) {
		if (jwt == null) {
			LOG.warn("No JWT supplied, running tests are we?");
		} else {
			if (LOG.isDebugEnabled()) {
				URL issuer = jwt.getIssuer();
				List<String> audience = jwt.getAudience();
				Object subject = jwt.getClaims().get("sub");
				Object scopes = jwt.getClaims().get("scope");
				Object expires = jwt.getClaims().get("exp");
				
				LOG.debug("Authorization info: Subject {}, scopes: {}, expires: {}, issuer: {}, audience: {}", subject, scopes, expires, issuer, audience);
			}
		}
	}
	
	private <T> T observationWithProductInfo(int productInfo, Supplier<T> supplier) {
		return observationUtil.observe("composite observation", "product info", "productId", String.valueOf(productInfo), supplier);
	}
}
