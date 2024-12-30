package com.david.microservices.alpha.composite.product.services.tracing;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

@Component
public class ObservationUtil {
	
	private final ObservationRegistry registry;
	
	public ObservationUtil(ObservationRegistry registry) {
		this.registry = registry;
		
	}

	public <T> T observe(String observationName, String contextualName, String highCadinalityKey, String highCardinalityValue, Supplier<T> supplier) {
		return Observation.createNotStarted(observationName, registry)
				.contextualName(contextualName)
				.highCardinalityKeyValue(highCadinalityKey, highCardinalityValue)
				.observe(supplier);
	}
}
