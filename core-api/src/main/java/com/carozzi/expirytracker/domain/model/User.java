package com.carozzi.expirytracker.domain.model;

import java.util.UUID;

public record User(UUID id, String username, String email, String password) {
  public User {
    // Validaciones de Existencia (Fail-fast)
    if (id == null)
      throw new IllegalArgumentException("ID obligatorio");

    // Validación de Contenido
    if (username == null || username.isBlank() || username.length() < 2) {
      throw new IllegalArgumentException("El nombre de usuario debe tener al menos 2 caracteres");
    }

    if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new IllegalArgumentException("El formato del email es inválido");
    }

    // La contraseña en el dominio puede ser larga (hasheada),
    // pero nunca nula ni vacía.
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacía");
    }
  }
}
