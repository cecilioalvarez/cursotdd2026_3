package com.arquitecturajava.web1.rest;

import java.math.BigDecimal;

import com.arquitecturajava.web1.negocio.Curso;

/**
 * JSON que devuelve el servicio REST. No se expone la entidad directamente
 * para no sacar sus imparticiones ni atar el JSON al modelo de JPA.
 */
public record CursoResponse(Long id, String titulo, String autor, double precio, BigDecimal precioConIva) {

	public static CursoResponse of(Curso curso) {
		return new CursoResponse(curso.getId(), curso.getTitulo(), curso.getAutor(), curso.getPrecio(),
				curso.getPrecioConIva());
	}
}
