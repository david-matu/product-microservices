package com.dave.microservices.alpha.gateway;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.logging.Level.FINE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class HealthcheckConfiguration {
	
	private static final Logger LOG = LoggerFactory.getLogger(HealthcheckConfiguration.class);
	
	private WebClient webClient;
	
	@Autowired
	public HealthcheckConfiguration(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}
	
	@Bean
	ReactiveHealthContributor coreServices() {
		final Map<String, ReactiveHealthContributor> registry = new LinkedHashMap<>();
		
		registry.put("product", 		(ReactiveHealthIndicator) () -> getHealth("http://product"));
		registry.put("recommendation", 	(ReactiveHealthIndicator) () -> getHealth("http://recommendation"));
		registry.put("review", 			(ReactiveHealthIndicator) () -> getHealth("http://review"));
		
		return CompositeReactiveHealthContributor.fromMap(registry);
	}

	private Mono<Health> getHealth(String baseUrl) {
		String url = baseUrl + "/actuator/health";
		
		LOG.debug("Setting up a call to the Health API on url: {}", url);
		
		return webClient.get().uri(url)
				.retrieve()
				.bodyToMono(String.class)
				.map(s -> new Health.Builder().up().build())
				.onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
				.log(LOG.getName(), FINE);
		
	}
}
