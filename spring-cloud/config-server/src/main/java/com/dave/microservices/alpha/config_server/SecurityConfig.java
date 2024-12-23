package com.dave.microservices.alpha.config_server;

import static org.springframework.security.config.Customizer.withDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
private static Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);
	
	private String username;
	private String password;
	
	public SecurityConfig() {}
	
	/*
	public SecurityConfig(
			@Value("${app.env-username}") String username,
			@Value("${app.env-password}") String password) {
		this.username = username;
		this.password = password;
		
		LOG.info("Retrieved username and password from config: {}:{}", username, password);
	}
	*/
	
	/*
	@Bean
	InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user")
				.password("pwd")
				.roles("USER")
				.build();
		
		return new InMemoryUserDetailsManager(user);
				
	}
	*/
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		/*
		http
		 .csrf(csrf -> csrf.disable())		// Disable CSRF to enable POST to /encrypt and /decrypt endpoints
		 .authorizeHttpRequests(r -> {
			 r.anyRequest().authenticated();
		 })
		 .httpBasic(Customizer.withDefaults());
		*/
		
		http
			.csrf(c -> c.disable())		// Disable CSRF to enable POST to /encrypt and /decrypt endpoints
			.authorizeHttpRequests(r -> r.anyRequest().authenticated())
			.httpBasic(withDefaults());
		
		return http.build();
	}
	
	/*
	 * Dec 16, 2024
	 * Comment this method later on as I'm activating local user for debugging purpose
	 * 
	 */
	/*
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user")
				.password("pwd")
				.roles("USER")
				.build();
		
		return new InMemoryUserDetailsManager(user);
	}
	*/
}
