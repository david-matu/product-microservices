package com.david.microservices.alpha.composite.product.services.tracing;

import org.springframework.boot.info.BuildProperties;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationFilter;

public class BuildInfoObservationFilter implements ObservationFilter {

	private final BuildProperties buildProperties; 
	
	public BuildInfoObservationFilter(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Override
	public Context map(final Observation.Context context) {
		KeyValue buildVersion = KeyValue.of("build.version", buildProperties.getVersion()); 
		
		return context.addLowCardinalityKeyValue(buildVersion);
	}

}
