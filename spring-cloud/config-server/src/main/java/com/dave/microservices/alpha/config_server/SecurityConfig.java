package com.dave.microservices.alpha.config_server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
		 .csrf(csrf -> csrf.disable())		// Disable CSRF to enable POST to /encrypt and /decrypt endpoints
		 .authorizeHttpRequests(r -> {
			 r.anyRequest().authenticated();
		 })
		 .httpBasic(Customizer.withDefaults());
		
		return http.build();
	}
}
