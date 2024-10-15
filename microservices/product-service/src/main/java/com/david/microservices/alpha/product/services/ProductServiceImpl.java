package com.david.microservices.alpha.product.services;

import static java.util.logging.Level.FINE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import com.david.microservices.alpha.api.core.product.Product;
import com.david.microservices.alpha.api.core.product.ProductService;
import com.david.microservices.alpha.api.exceptions.InvalidInputException;
import com.david.microservices.alpha.api.exceptions.NotFoundException;
import com.david.microservices.alpha.product.persistence.ProductEntity;
import com.david.microservices.alpha.product.persistence.ProductRepository;
import com.david.microservices.alpha.util.http.ServiceUtil;

import reactor.core.publisher.Mono;

@RestController
public class ProductServiceImpl implements ProductService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
	
	private final ServiceUtil serviceUtil;
	
	private final ProductRepository repo;
	private final ProductMapper mapper;
	
	@Autowired
	public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
		this.repo = repository;
		this.mapper = mapper;
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public Mono<Product> getProduct(int productId) {
		
		LOG.debug("/product return the found product for productId={}", productId);
		
		if(productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}
		
		return repo.findByProductId(productId)
				.switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
				.log(LOG.getName(), FINE)
				.map(e -> mapper.entityToApi(e))
				.map(e -> setServiceAddress(e));
	}

	private Product setServiceAddress(Product e) {
		e.setServiceAddress(serviceUtil.getServiceAddress());
		return e;
	}
	
	@Override
	public Mono<Product> createProduct(Product body) {
		if(body.getProductId() < 1) {
			throw new InvalidInputException("Invalid productId: " + body.getProductId());
		}
		
		ProductEntity entity = mapper.apiToEntity(body);
		
		Mono<Product> newEntity = repo.save(entity)
				.log(LOG.getName(), FINE)
				.onErrorMap(DuplicateKeyException.class, ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
				.map(e -> mapper.entityToApi(e));
		
		return newEntity;
	}

	@Override
	public Mono<Void> deleteProduct(int productId) {
		if(productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}
		
		LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
		
		//.ifPresent(e -> repo.delete(e));
		return repo.findByProductId(productId)
				.log(LOG.getName(), FINE)
				.map(e -> repo.delete(e))
				.flatMap(e -> e);
	}
	
	/*
	@Override
	public Product createProduct(Product body) {
		try {
			ProductEntity entity = mapper.apiToEntity(body);
			ProductEntity newEntity = repo.save(entity);
			
			LOG.debug("createProduct: entity created for productId: {}", body.getProductId());
			return mapper.entityToApi(newEntity);
		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
		}
	}

	@Override
	public void deleteProduct(int productId) {
		LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
		repo.findByProductId(productId).ifPresent(e -> repo.delete(e));
	}
	*/
}
