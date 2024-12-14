package com.dave.microservices.alpha.gateway.security;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		
		/*
		http
			.authorizeExchange()
			.pathMatchers("/actuator/**").permitAll()
			.anyExchange().authenticated()
			.and()
			.oauth2ResourceServer()
			.jwt();
		*/
		
		http
		.csrf(csrf -> csrf.disable())
        .authorizeExchange(exchange -> exchange
        		.pathMatchers("/headerrouting/**").permitAll()
        		.pathMatchers("/actuator/**").permitAll()
        		.pathMatchers("/eureka/**").permitAll()
        		.pathMatchers("/oauth2/**").permitAll()
        		.pathMatchers("/login/**").permitAll()
        		.pathMatchers("/error/**").permitAll()
        		.pathMatchers("/openapi/**").permitAll()
        		.pathMatchers("/webjars/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/config/**").permitAll()
                .anyExchange().authenticated()
                ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        
		return http.build();
	}
}
