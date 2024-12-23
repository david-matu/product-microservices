package com.dave.microservices.alpha.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class GatewayAppConfigs {
	
	private static final Logger LOG = LoggerFactory.getLogger(GatewayAppConfigs.class);
	
	private final String eureka_username;
	private final String eureka_password;
	private final String eureka_server;
	
	public GatewayAppConfigs(@Value("${app.eureka-username}") String eureka_username, @Value("${app.eureka-password}") String eureka_password, @Value("${app.eureka-server}") String eureka_server) { 
		this.eureka_username = eureka_username;
		this.eureka_password = eureka_password;
		this.eureka_server = eureka_server;
		
		LOG.info("Eureka info loaded from configs -- host: {} -|- username password: {}/{}", eureka_server, eureka_username, eureka_password);
	}
	
	
	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		LOG.info("Username and password for basic auth to Eureka Discovery server: {}/{}", this.eureka_username, this.eureka_password);
		
		// return WebClient.builder();
		
		return WebClient.builder()
				.filter((request, next) -> {
					String requestInfo = request.method().toString() + "/ " + request.url() + " Headers: " + request.headers();
					String requestBody = request.toString();
					
					String host = UriComponentsBuilder.fromUri(request.url())
							.build()
							.getHost();
					
					String path = UriComponentsBuilder.fromUri(request.url())
					.build().getPath();
					
					int port = UriComponentsBuilder.fromUri(request.url())
							.build().getPort();
					
					String schem = UriComponentsBuilder.fromUri(request.url())
							.build().getScheme();
					
					String userInfo = UriComponentsBuilder.fromUri(request.url())
							.build().getUserInfo();
					
					LOG.info("Resolved scheme, host, path from url is: {}://{}:{}{}", schem, host, port, path);
					LOG.info("User info: {}\nRequest url: {}\nRequest body: {}", userInfo, requestInfo, requestBody);
					
					if (host != null && host.equals(this.eureka_server)) {	// System.getenv("app.eureka-server")
						return next.exchange(
								ClientRequest.from(request)
								.headers(headers -> headers.setBasicAuth(eureka_username, eureka_password))
								.build()
						);
					}
					return next.exchange(request);
				});
	}
	
}
