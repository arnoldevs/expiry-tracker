package com.carozzi.expirytracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // <--- Esto busca el archivo application-test.yml
class ExpiryTrackerCoreApplicationTests {

	@Test
	void contextLoads() {
		// Este test ahora pasará en milisegundos sin mirar Docker
	}
}