package com.david.microservices.alpha.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.david.microservices.alpha")
public class ReviewServiceApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceApplication.class);
	
	public static void main(String[] args) {
		//SpringApplication.run(ReviewServiceApplication.class, args);
		ConfigurableApplicationContext ctx = SpringApplication.run(ReviewServiceApplication.class, args);
		
		String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		LOG.info("Connected to MySQL: " + mysqlUri);
	}
}