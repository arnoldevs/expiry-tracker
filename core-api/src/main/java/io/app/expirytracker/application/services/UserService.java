package io.app.expirytracker.application.services;

import com.fasterxml.uuid.Generators;

import io.app.expirytracker.application.ports.in.CreateUserUseCase;
import io.app.expirytracker.application.ports.out.UserRepositoryPort;
import io.app.expirytracker.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements CreateUserUseCase {

	private final UserRepositoryPort userRepository;

	@Override
	@Transactional
	public User create(CreateUserCommand command) {
		// Validaciones de negocio (Unicidad)
		if (userRepository.existsByEmail(command.email())) {
			throw new IllegalArgumentException("El email ya está registrado");
		}

		// Generación de identidad tecnológica (UUID v7)
		UUID userId = Generators.timeBasedEpochGenerator().generate();

		// Creación del objeto de Dominio
		User user = new User(
				userId,
				command.username(),
				command.email(),
				command.password() // Nota: Aquí agregaremos encriptación
		);

		// Persistencia
		return userRepository.save(user);
	}
}