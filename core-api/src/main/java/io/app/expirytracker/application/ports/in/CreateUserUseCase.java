package io.app.expirytracker.application.ports.in;

import io.app.expirytracker.domain.model.User;

public interface CreateUserUseCase {

  // Definimos qué datos necesitamos estrictamente para crear
  record CreateUserCommand(
      String username,
      String email,
      String password) {
  }

  // El contrato recibe el comando, no el objeto de dominio completo
  User create(CreateUserCommand command);
}