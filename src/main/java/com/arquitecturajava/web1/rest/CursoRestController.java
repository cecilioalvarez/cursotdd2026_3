package com.arquitecturajava.web1.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arquitecturajava.web1.servicios.CursoService;

import jakarta.validation.Valid;

/**
 * Servicio REST de cursos: listado y alta.
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

	private final CursoService cursoService;

	public CursoRestController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	@GetMapping
	public List<CursoResponse> listar() {
		return cursoService.buscarTodos().stream()
				.map(CursoResponse::of)
				.toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CursoResponse crear(@Valid @RequestBody CursoRequest request) {
		return CursoResponse.of(cursoService.guardar(request.toCurso()));
	}

	// 400 con el mensaje de cada campo incorrecto: {"titulo": "El título es obligatorio"}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> erroresDeValidacion(MethodArgumentNotValidException excepcion) {
		return excepcion.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
						(primero, segundo) -> primero));
	}
}
