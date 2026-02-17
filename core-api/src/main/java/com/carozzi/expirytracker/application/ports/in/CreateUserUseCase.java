package com.carozzi.expirytracker.application.ports.in;

import com.carozzi.expirytracker.domain.model.User;

public interface CreateUserUseCase {
  // El "Contrato" de lo que un usuario externo puede hacer
  User create(User user);
}
