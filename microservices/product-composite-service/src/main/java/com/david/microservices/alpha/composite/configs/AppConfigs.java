package com.david.microservices.alpha.composite.configs;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.david.microservices.alpha.composite.product.services.ProductCompositeIntegration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class AppConfigs {
	
	@Autowired
	ProductCompositeIntegration integration;
	
	@Value("${api.common.title}") String apiTitle;
	@Value("${api.common.description}") String apiDescription;
	@Value("${api.common.version}") String apiVersion;
	@Value("${api.common.contact.name}") String apiContactName;
	@Value("${api.common.contact.url}") String apiContactUrl;
	@Value("${api.common.contact.email}") String apiContactEmail;
	@Value("${api.common.termsOfService}") String apiTermsOfService;
	@Value("${api.common.license}") String apiLicense;
	@Value("${api.common.licenseUrl}") String apiLicenseUrl;
	@Value("${api.common.externalDocDesc}") String apiExternalDocDesc;
	@Value("${api.common.externalDocUrl}") String apiExternaDocUrl;
	// @Value("${api.common.}") String ;
	
	@Bean
	public OpenAPI getOpenAPIDocumentation() {
		return new OpenAPI()
					.info(new Info().title(apiTitle)
							.description(apiDescription)
							.version(apiVersion)
							.contact(new Contact()
									.name(apiContactName)
									.url(apiContactUrl)
									.email(apiContactEmail))
							.termsOfService(apiTermsOfService)
							.license(new License()
									.name(apiLicense)
									.url(apiLicenseUrl)))
					.externalDocs(new ExternalDocumentation()
							.description(apiExternalDocDesc)
							.url(apiExternaDocUrl));
					
	}
	
	/*
	@Bean
	ReactiveHealthContributor coreServices() {
		final Map<String, ReactiveHealthContributor> registry = new LinkedHashMap<>();
		
		registry.put("product", (ReactiveHealthIndicator) () -> integration.getProductHealth());
		registry.put("recommendation", (ReactiveHealthIndicator) () -> integration.getRecommendationHealth());
		registry.put("review", (ReactiveHealthIndicator) () -> integration.getReviewHealth());
		
		return CompositeReactiveHealthContributor.fromMap(registry);
	}
	*/
	
}
