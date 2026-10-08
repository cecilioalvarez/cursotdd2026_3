package com.arquitecturajava.web1.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;
import com.arquitecturajava.web1.repositorios.CursoRepository;
import com.arquitecturajava.web1.repositorios.ImparticionRepository;
import com.arquitecturajava.web1.selenium.helpers.DatosCurso;
import com.arquitecturajava.web1.selenium.helpers.DatosImparticion;
import com.arquitecturajava.web1.selenium.helpers.FilaCurso;
import com.arquitecturajava.web1.selenium.helpers.FilaImparticion;
import com.arquitecturajava.web1.selenium.helpers.SeleniumTestBase;
import com.arquitecturajava.web1.selenium.paginas.FormularioCursoPage;
import com.arquitecturajava.web1.selenium.paginas.FormularioImparticionPage;
import com.arquitecturajava.web1.selenium.paginas.ListadoCursosPage;
import com.arquitecturajava.web1.selenium.paginas.ListadoImparticionesPage;

/**
 * Recorrido completo: el usuario crea un curso y, a continuación, le da de
 * alta una impartición.
 */
@DisplayName("Alta de un curso y de su primera impartición desde el navegador")
class CursoConImparticionSeleniumTest extends SeleniumTestBase {

	private static final DatosCurso CURSO = new DatosCurso("Curso con imparticiones", "Elena", 300);
	private static final DatosImparticion IMPARTICION = new DatosImparticion(
			"Noviembre 2026", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6));

	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private ImparticionRepository imparticionRepository;

	@Test
	@DisplayName("Crear un curso y después una impartición deja los dos dados de alta")
	void altaDeCursoYDeSuImparticion() {
		// Dado el listado de cursos
		ListadoCursosPage listadoCursos = ListadoCursosPage.abrir(driver, urlBase());

		// Cuando el usuario da de alta un curso
		FormularioCursoPage formularioCurso = listadoCursos.pulsarNuevoCurso();
		listadoCursos = formularioCurso.rellenar(CURSO).guardar();

		// Entonces el curso aparece en el listado
		assertThat(listadoCursos.cursos())
				.contains(new FilaCurso("Curso con imparticiones", "Elena", "300,00 €", "363,00 €"));

		// Cuando entra en sus imparticiones, que todavía no tiene
		ListadoImparticionesPage listadoImparticiones = listadoCursos.pulsarVerImparticiones(CURSO.titulo());
		assertThat(listadoImparticiones.titulo()).isEqualTo(ListadoImparticionesPage.TITULO);
		assertThat(listadoImparticiones.tituloDelCurso()).isEqualTo(CURSO.titulo());
		assertThat(listadoImparticiones.muestraAvisoSinImparticiones()).isTrue();

		// Y da de alta una impartición
		FormularioImparticionPage formularioImparticion = listadoImparticiones.pulsarNuevaImparticion();
		assertThat(formularioImparticion.titulo()).isEqualTo(FormularioImparticionPage.TITULO);
		assertThat(formularioImparticion.tituloDelCurso()).isEqualTo(CURSO.titulo());
		listadoImparticiones = formularioImparticion.rellenar(IMPARTICION).guardar();

		// Entonces la impartición aparece en el listado del curso
		assertThat(listadoImparticiones.tituloDelCurso()).isEqualTo(CURSO.titulo());
		assertThat(listadoImparticiones.muestraAvisoSinImparticiones()).isFalse();
		assertThat(listadoImparticiones.imparticiones())
				.containsExactly(new FilaImparticion("Noviembre 2026", "02/11/2026", "06/11/2026"));

		// Y al volver a cursos, el curso sigue ahí
		listadoCursos = listadoImparticiones.volverACursos();
		assertThat(listadoCursos.titulo()).isEqualTo(ListadoCursosPage.TITULO);
		assertThat(listadoCursos.cursos()).extracting(FilaCurso::titulo).contains(CURSO.titulo());

		// Y los dos están guardados en la base de datos, enlazados entre sí
		Curso curso = cursoRepository.findAll().stream()
				.filter(guardado -> guardado.getTitulo().equals(CURSO.titulo()))
				.findFirst()
				.orElseThrow();
		assertThat(curso)
				.extracting(Curso::getAutor, Curso::getPrecio)
				.containsExactly("Elena", 300.0);
		assertThat(imparticionRepository.findByCursoIdOrderByFechaInicio(curso.getId()))
				.singleElement()
				.extracting(Imparticion::getNombre, Imparticion::getFechaInicio, Imparticion::getFechaFin)
				.containsExactly("Noviembre 2026", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6));
	}
}
