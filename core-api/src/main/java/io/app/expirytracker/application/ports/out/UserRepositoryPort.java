package io.app.expirytracker.application.ports.out;

import java.util.Optional;
import java.util.UUID;

import io.app.expirytracker.domain.model.User;

/**
 * Este es el "Puerto de Salida".
 * Es el contrato que el dominio le exige a cualquier base de datos
 * que quiera trabajar con él.
 */
public interface UserRepositoryPort {

  // "Necesito guardar un usuario y que me devuelvas el resultado"
  User save(User user);

  // "Necesito buscar a alguien por su ID, pero puede que no exista (Optional)"
  Optional<User> findById(UUID id);

  // "Necesito buscar por nombre de usuario para el login"
  Optional<User> findByUsername(String username);

  // "Necesito saber si un email ya está registrado"
  boolean existsByEmail(String email);
}
