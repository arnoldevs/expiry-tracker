package com.carozzi.expirytracker.application.ports.in;

import com.carozzi.expirytracker.domain.model.User;

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