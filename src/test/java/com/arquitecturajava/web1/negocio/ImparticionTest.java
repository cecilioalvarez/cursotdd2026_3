package com.arquitecturajava.web1.negocio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ImparticionTest {

	private static final LocalDate INICIO = LocalDate.of(2026, 11, 2);
	private static final LocalDate FIN = LocalDate.of(2026, 11, 6);

	private static ValidatorFactory validatorFactory;
	private static Validator validator;

	@BeforeAll
	static void crearValidador() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@AfterAll
	static void cerrarValidador() {
		validatorFactory.close();
	}

	@Test
	void imparticionConDatosCorrectosEsValida() {
		Imparticion imparticion = new Imparticion("Noviembre 2026", INICIO, FIN);

		assertThat(validator.validate(imparticion)).isEmpty();
	}

	@Test
	void imparticionDeUnSoloDiaEsValida() {
		Imparticion imparticion = new Imparticion("Taller", INICIO, INICIO);

		assertThat(validator.validate(imparticion)).isEmpty();
	}

	@Test
	void fechaFinAnteriorAInicioNoEsValida() {
		Imparticion imparticion = new Imparticion("Noviembre 2026", FIN, INICIO);

		assertThat(validator.validate(imparticion))
				.extracting(ConstraintViolation::getMessage)
				.containsExactly("La fecha de fin no puede ser anterior a la de inicio");
	}

	@Test
	void nombreVacioNoEsValido() {
		Imparticion imparticion = new Imparticion("  ", INICIO, FIN);

		assertThat(validator.validate(imparticion))
				.extracting(ConstraintViolation::getMessage)
				.containsExactly("El nombre es obligatorio");
	}

	@Test
	void fechasObligatorias() {
		Imparticion imparticion = new Imparticion("Noviembre 2026", null, null);

		assertThat(validator.validate(imparticion))
				.extracting(ConstraintViolation::getMessage)
				.containsExactlyInAnyOrder(
						"La fecha de inicio es obligatoria",
						"La fecha de fin es obligatoria");
	}

	@Test
	void imparticionesConMismoIdSonIguales() {
		Imparticion imparticion1 = new Imparticion(1L, "Noviembre", INICIO, FIN);
		Imparticion imparticion2 = new Imparticion(1L, "Diciembre", INICIO, FIN);

		assertThat(imparticion1).isEqualTo(imparticion2);
		assertThat(imparticion1.hashCode()).isEqualTo(imparticion2.hashCode());
	}

	@Test
	void imparticionesSinIdNoSonIgualesEntreSi() {
		assertThat(new Imparticion("Noviembre", INICIO, FIN))
				.isNotEqualTo(new Imparticion("Noviembre", INICIO, FIN));
	}

	@Test
	void toStringIncluyeLosCamposSinElCurso() {
		Imparticion imparticion = new Imparticion(1L, "Noviembre 2026", INICIO, FIN);

		assertThat(imparticion.toString())
				.isEqualTo("Imparticion [id=1, nombre=Noviembre 2026, fechaInicio=2026-11-02, fechaFin=2026-11-06]");
	}
}
