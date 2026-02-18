package com.carozzi.expirytracker.infrastructure.adapters.in.web.dtos;

/**
 * DTO para el registro de nuevos usuarios.
 */
public record UserRequest(
		String username,
		String email,
		String password) {
}