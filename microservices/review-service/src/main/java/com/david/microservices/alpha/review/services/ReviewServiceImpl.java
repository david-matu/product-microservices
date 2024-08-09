package com.david.microservices.alpha.review.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.david.microservices.alpha.api.core.review.Review;
import com.david.microservices.alpha.api.core.review.ReviewService;
import com.david.microservices.alpha.api.exceptions.InvalidInputException;
import com.david.microservices.alpha.util.http.ServiceUtil;

@RestController
public class ReviewServiceImpl implements ReviewService {

	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
	
	private final ServiceUtil serviceUtil;
	
	@Autowired
	public ReviewServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}

	@Override
	public List<Review> getReviews(int productId) {
		
		if(productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}
		
		if(productId == 213) {
			LOG.debug("No review found for productId: {}", productId);
			return new ArrayList<>();
		}
		
		List<Review> list = new ArrayList<>();
		list.add(new Review(productId, 1, "Author 1", "Subject A", "Content A1", serviceUtil.getServiceAddress()));
		list.add(new Review(productId, 2, "Author 2", "Subject B", "Content B1", serviceUtil.getServiceAddress()));
		list.add(new Review(productId, 3, "Author 3", "Subject C", "Content C1", serviceUtil.getServiceAddress()));
		
		LOG.debug("/reviews response size: {}", list.size());
		
		return list;
	}
	
}
