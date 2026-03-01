package io.app.expirytracker.infrastructure.adapters.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.app.expirytracker.application.ports.in.CreateUserUseCase;
import io.app.expirytracker.application.ports.in.CreateUserUseCase.CreateUserCommand;
import io.app.expirytracker.domain.model.User;
import io.app.expirytracker.infrastructure.adapters.in.web.dtos.UserRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final CreateUserUseCase createUserUseCase;

	@PostMapping
	public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest request) {
		// Mapeamos el DTO al Command (Puerto de entrada)
		var command = new CreateUserCommand(
				request.username(),
				request.email(),
				request.password());

		User createdUser = createUserUseCase.create(command);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}
}