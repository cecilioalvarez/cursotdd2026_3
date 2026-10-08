package com.arquitecturajava.web1.negocio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CursoTest {

	@ParameterizedTest
	@CsvSource({
			"100, 121.00",
			"0, 0.00",
			"10.50, 12.71",
			"19.99, 24.19",
			"0.01, 0.01"
	})
	void precioConIvaSeCalculaConDosDecimales(double precio, String esperado) {
		Curso curso = new Curso("Java", "Cecilio", precio);

		assertThat(curso.getPrecioConIva()).isEqualTo(new BigDecimal(esperado));
	}

	@Test
	void precioConIvaRedondeaHaciaArribaEnLaMitad() {
		// 2.50 * 1.21 = 3.025 -> 3.03
		Curso curso = new Curso("Java", "Cecilio", 2.50);

		assertThat(curso.getPrecioConIva()).isEqualTo(new BigDecimal("3.03"));
	}

	@Test
	void cursosConMismoIdSonIguales() {
		Curso curso1 = new Curso(1L, "Java", "Cecilio", 100);
		Curso curso2 = new Curso(1L, "Spring", "Ana", 50);

		assertThat(curso1).isEqualTo(curso2);
		assertThat(curso1.hashCode()).isEqualTo(curso2.hashCode());
	}

	@Test
	void cursosConDistintoIdNoSonIguales() {
		Curso curso1 = new Curso(1L, "Java", "Cecilio", 100);
		Curso curso2 = new Curso(2L, "Java", "Cecilio", 100);

		assertThat(curso1).isNotEqualTo(curso2);
	}

	@Test
	void cursosSinIdNoSonIgualesEntreSi() {
		Curso curso1 = new Curso("Java", "Cecilio", 100);
		Curso curso2 = new Curso("Java", "Cecilio", 100);

		assertThat(curso1).isNotEqualTo(curso2);
	}

	@Test
	void cursoEsIgualASiMismo() {
		Curso curso = new Curso("Java", "Cecilio", 100);

		assertThat(curso).isEqualTo(curso);
	}

	@Test
	void cursoNoEsIgualANullNiAOtroTipo() {
		Curso curso = new Curso(1L, "Java", "Cecilio", 100);

		assertThat(curso).isNotEqualTo(null);
		assertThat(curso).isNotEqualTo("Java");
	}

	@Test
	void cursoNuevoNoTieneImparticiones() {
		assertThat(new Curso("Java", "Cecilio", 100).getImparticiones()).isEmpty();
	}

	@Test
	void addImparticionLaAnadeYLaEnlazaConElCurso() {
		Curso curso = new Curso("Java", "Cecilio", 100);
		Imparticion imparticion = new Imparticion("Noviembre", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6));

		curso.addImparticion(imparticion);

		assertThat(curso.getImparticiones()).containsExactly(imparticion);
		assertThat(imparticion.getCurso()).isSameAs(curso);
	}

	@Test
	void removeImparticionLaQuitaYLaDesenlazaDelCurso() {
		Curso curso = new Curso("Java", "Cecilio", 100);
		Imparticion imparticion = new Imparticion("Noviembre", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6));
		curso.addImparticion(imparticion);

		curso.removeImparticion(imparticion);

		assertThat(curso.getImparticiones()).isEmpty();
		assertThat(imparticion.getCurso()).isNull();
	}

	@Test
	void imparticionesNoSePuedenModificarDesdeFuera() {
		Curso curso = new Curso("Java", "Cecilio", 100);

		assertThatThrownBy(() -> curso.getImparticiones().add(new Imparticion()))
				.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void toStringIncluyeTodosLosCampos() {
		Curso curso = new Curso(1L, "Java", "Cecilio", 100);

		assertThat(curso.toString())
				.isEqualTo("Curso [id=1, titulo=Java, autor=Cecilio, precio=100.0]");
	}
}
