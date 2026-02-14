package com.carozzi.expirytracker.domain.model;

import java.util.Objects;

public record User(Long id, String username, String email, String password) {
  public User {
    // Nulidad
    Objects.requireNonNull(username, "Nombre de usuario obligatorio");
    Objects.requireNonNull(email, "Email obligatorio");
    Objects.requireNonNull(password, "Contraseña obligatoria");

    // Validación básica
    if (username.isBlank())
      throw new IllegalArgumentException("Nombre de usuario vacío");
    if (!email.contains("@"))
      throw new IllegalArgumentException("Email no válido");
    if (password.length() < 8)
      throw new IllegalArgumentException("Contraseña demasiado corta");
  }
}
