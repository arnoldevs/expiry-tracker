package io.app.expirytracker.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Listener
public class AuditConfig {

	/**
	 * Este Bean le dice a JPA quién es el usuario actual.
	 * FASE 1 (Sin Seguridad): Devolvemos un usuario fijo.
	 * FASE 2 (Futuro): Aquí conectaremos con Spring Security para sacar el usuario
	 * real del Token.
	 */
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of("DEV_USER");
	}
}