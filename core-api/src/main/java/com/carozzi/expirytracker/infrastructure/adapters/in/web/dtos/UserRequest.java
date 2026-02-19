package com.carozzi.expirytracker.infrastructure.adapters.in.web.dtos;

import jakarta.validation.constraints.*;

/**
 * DTO para el registro de nuevos usuarios.
 */

public record UserRequest(
		@NotBlank(message = "El nombre de usuario es obligatorio") @Size(min = 2, max = 20, message = "El usuario debe tener entre 2 y 20 caracteres") @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El usuario solo permite letras, números, puntos y guiones") String username,

		@NotBlank(message = "El email es obligatorio") @Email(message = "El formato del email no es válido") String email,

		@NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password) {
}