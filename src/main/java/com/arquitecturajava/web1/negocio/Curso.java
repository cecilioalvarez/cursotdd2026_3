package com.arquitecturajava.web1.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Curso {

	public static final BigDecimal IVA = new BigDecimal("0.21");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "El título es obligatorio")
	private String titulo;
	@NotBlank(message = "El autor es obligatorio")
	private String autor;
	private double precio;

	public Curso() {
	}

	public Curso(String titulo, String autor, double precio) {
		this.titulo = titulo;
		this.autor = autor;
		this.precio = precio;
	}

	public Curso(Long id, String titulo, String autor, double precio) {
		this.id = id;
		this.titulo = titulo;
		this.autor = autor;
		this.precio = precio;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public BigDecimal getPrecioConIva() {
		return BigDecimal.valueOf(precio)
				.multiply(BigDecimal.ONE.add(IVA))
				.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Curso otro))
			return false;
		return id != null && id.equals(otro.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Curso [id=" + id + ", titulo=" + titulo + ", autor=" + autor + ", precio=" + precio + "]";
	}
}
