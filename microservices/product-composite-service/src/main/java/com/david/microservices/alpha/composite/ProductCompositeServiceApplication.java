package com.david.microservices.alpha.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@ComponentScan("com.david.microservices.alpha")
public class ProductCompositeServiceApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceApplication.class);
	
	/*
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	*/
	
	private final Integer threadPoolSize;
	private final Integer taskQueueSize;
	
	public ProductCompositeServiceApplication(@Value("${app.threadPoolSize:10}") Integer threadPoolSize, @Value("${app.taskQueueSize:100}") Integer taskQueueSize) {
		this.threadPoolSize = threadPoolSize;
		this.taskQueueSize = taskQueueSize;
	}
	
	@Bean
	public Scheduler publishEventScheduler() {
		LOG.info("Creates a messagingScheduler with connectionPoolSize = {}", threadPoolSize);
		return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
	}
	
	/*
	 * 
	 * Instead of this, replace with a load-balancer-aware exchange-filter function
	 * 
	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalanceWebClieBuilder() {
		return WebClient.builder();
	}
	*
	*/
	
	@Autowired
	private ReactorLoadBalancerExchangeFilterFunction lbFunction;
	
	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.filter(lbFunction).build();
	}
	
	public static void main(String[] args) {
		
		// Turn on automatic context propagation to overcome limitation of reactive clients
		Hooks.enableAutomaticContextPropagation();
		
		SpringApplication.run(ProductCompositeServiceApplication.class, args);
	}

}
