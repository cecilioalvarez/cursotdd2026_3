package com.arquitecturajava.web1.negocio;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Una edición concreta de un curso, con sus fechas de inicio y fin. Un curso
 * tiene n imparticiones; la relación la gestiona {@link Curso#addImparticion}.
 */
@Entity
public class Imparticion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;
	// ISO (2026-11-02) es el formato que envía y espera un <input type="date">
	@NotNull(message = "La fecha de inicio es obligatoria")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate fechaInicio;
	@NotNull(message = "La fecha de fin es obligatoria")
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate fechaFin;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "curso_id")
	private Curso curso;

	public Imparticion() {
	}

	public Imparticion(String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
		this.nombre = nombre;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public Imparticion(Long id, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
		this.id = id;
		this.nombre = nombre;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Curso getCurso() {
		return curso;
	}

	// Solo desde Curso, para que los dos lados de la relación no se desincronicen
	void setCurso(Curso curso) {
		this.curso = curso;
	}

	// Si falta alguna fecha ya lo indican los @NotNull
	@AssertTrue(message = "La fecha de fin no puede ser anterior a la de inicio")
	public boolean isFechasEnOrden() {
		return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Imparticion otra))
			return false;
		return id != null && id.equals(otra.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	// Sin el curso: es una relación perezosa y no queremos cargarla al hacer log
	@Override
	public String toString() {
		return "Imparticion [id=" + id + ", nombre=" + nombre + ", fechaInicio=" + fechaInicio
				+ ", fechaFin=" + fechaFin + "]";
	}
}
