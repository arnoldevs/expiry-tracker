package com.carozzi.expirytracker.infrastructure.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

	/**
	 * Captura errores de búsqueda cuando un recurso específico no existe.
	 * * Se dispara principalmente cuando un {@link java.util.Optional} está vacío
	 * y se intenta acceder a su contenido, o cuando se busca un UUID que no
	 * figura en la base de datos.
	 *
	 * @param ex La excepción de tipo NoSuchElementException capturada.
	 * @return Un {@link ResponseEntity} con código 404 (Not Found) y un cuerpo
	 *         JSON detallando el recurso faltante.
	 */
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Object> handleNotFound(NoSuchElementException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", "Recurso no encontrado");
		body.put("message", ex.getMessage());

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	/**
	 * Captura los errores de validación de los DTOs (@Valid).
	 * Traduce las anotaciones de Jakarta (comodines) a una respuesta 400.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Error de Validación de Entrada");

		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(" | "));

		body.put("message", message);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Captura errores cuando el tipo de dato enviado en la URL no coincide con el
	 * esperado.
	 * Ejemplo: Enviar texto en un campo de fecha o número.
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Parámetro con formato inválido");

		String parameterName = ex.getName();

		// El tipo lo "traducimos" para una mejor experiencia de usuario
		String typeNeeded = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "";
		String friendlyType = switch (typeNeeded) {
			case "Integer", "Long" -> "un número entero";
			case "LocalDate" -> "una fecha (AAAA-MM-DD)";
			case "Boolean" -> "un valor booleano (true/false)";
			default -> "el formato correcto";
		};

		body.put("message", String.format("El parámetro '%s' requiere %s.", parameterName, friendlyType));

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}