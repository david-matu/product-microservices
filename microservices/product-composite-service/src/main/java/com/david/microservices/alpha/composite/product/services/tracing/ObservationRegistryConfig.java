package com.david.microservices.alpha.composite.product.services.tracing;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

import io.micrometer.observation.ObservationRegistry;

@Configuration(proxyBeanMethods = false)
public class ObservationRegistryConfig implements ObservationRegistryCustomizer<ObservationRegistry> {
	
	private final BuildProperties buildProperties;
	
	public ObservationRegistryConfig(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Override
	public void customize(final ObservationRegistry registry) {
		registry.observationConfig().observationFilter(new BuildInfoObservationFilter(buildProperties));
	}
	
}
