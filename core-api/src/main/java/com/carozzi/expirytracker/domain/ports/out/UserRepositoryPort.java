package com.carozzi.expirytracker.domain.ports.out;

import com.carozzi.expirytracker.domain.model.User;
import java.util.Optional;

/**
 * Este es el "Puerto de Salida".
 * Es el contrato que el dominio le exige a cualquier base de datos
 * que quiera trabajar con él.
 */
public interface UserRepositoryPort {

  // "Necesito guardar un usuario y que me devuelvas el resultado"
  User save(User user);

  // "Necesito buscar a alguien por su ID, pero puede que no exista (Optional)"
  Optional<User> findById(Long id);

  // "Necesito buscar por nombre de usuario para el login"
  Optional<User> findByUsername(String username);

  // "Necesito saber si un email ya está registrado"
  boolean existsByEmail(String email);
}
