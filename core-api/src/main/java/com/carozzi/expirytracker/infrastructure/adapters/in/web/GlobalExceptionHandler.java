package com.carozzi.expirytracker.infrastructure.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Captura las excepciones de toda la capa Web para devolver respuestas JSON
 * limpias.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Captura las excepciones de lógica de negocio (lote duplicado, campos
	 * inválidos, etc.)
	 * que lanzamos como IllegalArgumentException en el Dominio o Servicio.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Validación de Negocio");
		body.put("message", ex.getMessage());

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Captura errores inesperados para no mostrar trazas de código (stacktraces) al
	 * cliente.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGlobalException(Exception ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", "Error Interno del Servidor");
		body.put("message", "Ocurrió un error inesperado. Por favor, contacte al administrador.");

		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}