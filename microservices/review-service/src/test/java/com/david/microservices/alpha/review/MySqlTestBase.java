package com.david.microservices.alpha.review;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public abstract class MySqlTestBase {
	
	// Declare database container with an extended wait period of 5 minutes for the container to start up
	private static JdbcDatabaseContainer database = new MySQLContainer<>("mysql:8.0.32").withStartupTimeoutSeconds(300);
	
	// Start the database container before any JUnit code is invoked
	static {
		database.start();
	}
	
	/**
	 * 
	 * @param registry
	 * 
	 * Get properties dynamically from the started container and override properties in the properties file
	 */
	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", database::getJdbcUrl);
		registry.add("spring.datasource.username", database::getUsername);
		registry.add("spring.datasource.password", database::getPassword);
	}
}
