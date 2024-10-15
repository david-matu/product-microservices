package com.david.microservices.alpha.composite;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;

public class TestFlux {
	
	@Test
	void testFlux() {
		List<Integer> list = Flux.just(1, 2, 3, 4)
				.filter(n -> n % 2 == 0)
				.map(n -> n * 2)
				.log()
				.collectList().block();
		
		assertThat(list).containsExactly(4, 8);
	}
}
