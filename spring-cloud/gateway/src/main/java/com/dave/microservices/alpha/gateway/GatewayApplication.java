package com.dave.microservices.alpha.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(GatewayApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	
	/*
	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		String eureka_username = System.getenv("app.eureka-username");
		String eureka_password = System.getenv("app.eureka-password");
		
		LOG.info("Username and password for basic auth to Eureka Discovery server: {}/{}", eureka_username, eureka_password);
		
		// return WebClient.builder();
		
		return WebClient.builder()
				.filter((request, next) -> {
					String host = UriComponentsBuilder.fromUri(request.url())
							.build()
							.getHost();
					
					LOG.info("Resolved host from url is: {}", host);
					
					if (host != null && host.equals(System.getenv("app.eureka-server"))) {
						return next.exchange(
								ClientRequest.from(request)
								.headers(headers -> headers.setBasicAuth(eureka_username, eureka_password))
								.build()
						);
					}
					return next.exchange(request);
				});
	}
	*/
}
