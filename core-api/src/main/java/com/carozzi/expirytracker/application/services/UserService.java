package com.carozzi.expirytracker.application.services;

import com.carozzi.expirytracker.application.ports.in.CreateUserUseCase;
import com.carozzi.expirytracker.application.ports.out.UserRepositoryPort;
import com.carozzi.expirytracker.domain.model.User;
import com.fasterxml.uuid.Generators;
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