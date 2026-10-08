package com.arquitecturajava.web1.rest;

import com.arquitecturajava.web1.negocio.Curso;

import jakarta.validation.constraints.NotBlank;

/**
 * JSON que recibe el alta de un curso. Sin id: lo asigna la base de datos.
 */
public record CursoRequest(
		@NotBlank(message = "El título es obligatorio") String titulo,
		@NotBlank(message = "El autor es obligatorio") String autor,
		double precio) {

	public Curso toCurso() {
		return new Curso(titulo, autor, precio);
	}
}
