package com.dave.microservices.alpha.discovery.eurekaserver.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
	
	private static Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);
	
	private String username;
	private String password;
	
	public SecurityConfig(
			@Value("${app.eureka-username}") String username,
			@Value("${app.eureka-password}") String password) {
		this.username = username;
		this.password = password;
	}
	
	@Bean
	InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username(username)
				.password(password)
				.roles("USER")
				.build();
		
		return new InMemoryUserDetailsManager(user);
				
	}
	
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {

        // Disable CSRF to allow services register themselves with Eureka
        /*
	     http
	        .csrf().disable()
	        .authorizeHttpRequests()
	            .anyRequest().authenticated()
	        .and()
	        .httpBasic();
	     */
        
		/*
		http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> {
					try {
						requests
						        .anyRequest().authenticated()
						        .and()
						        .httpBasic();
					} catch (Exception e) {
						// e.printStackTrace();
						LOG.error("There was an error with security configuration: {}", e.getMessage());
					}
				});
		*/
		
		http
			.csrf(c -> c.disable())
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/css/*", "/js/*")
					.permitAll()
					.anyRequest().authenticated())
			.httpBasic(withDefaults());
		
		return http.build();
		
	}
}
