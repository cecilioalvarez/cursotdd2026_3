package com.arquitecturajava.web1.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.repositorios.CursoRepository;
import com.arquitecturajava.web1.selenium.paginas.ConfirmacionBorrado;
import com.arquitecturajava.web1.selenium.paginas.DatosCurso;
import com.arquitecturajava.web1.selenium.paginas.FilaCurso;
import com.arquitecturajava.web1.selenium.paginas.FormularioCursoPage;
import com.arquitecturajava.web1.selenium.paginas.ListadoCursosPage;

/**
 * Recorridos del usuario sobre la gestión de cursos. El arranque de la
 * aplicación y del navegador lo hace {@link SeleniumTestBase}; los elementos
 * de cada pantalla están en sus Page Objects.
 */
@DisplayName("Gestión de cursos desde el navegador")
class CursoSeleniumTest extends SeleniumTestBase {

	private static final DatosCurso CURSO_NUEVO = new DatosCurso("Selenium WebDriver", "Marta", 200);
	private static final DatosCurso CURSO_ORIGINAL = new DatosCurso("Curso para editar", "Pedro", 100);
	private static final DatosCurso CURSO_EDITADO = new DatosCurso("Curso editado con Selenium", "Lucía", 150);
	private static final DatosCurso CURSO_A_BORRAR = new DatosCurso("Curso para borrar", "Rosa", 50);

	@Autowired
	private CursoRepository cursoRepository;

	@Test
	@DisplayName("Dar de alta un curso lo añade al listado")
	void altaDeCurso() {
		// Dado el listado de cursos
		ListadoCursosPage listado = abrirListado();
		int cursosAntes = listado.numeroDeCursos();

		// Cuando el usuario rellena el formulario de alta y guarda
		FormularioCursoPage formulario = listado.pulsarNuevoCurso();
		assertThat(formulario.titulo()).isEqualTo(FormularioCursoPage.TITULO_ALTA);
		listado = formulario.rellenar(CURSO_NUEVO).guardar();

		// Entonces vuelve al listado y aparece el curso nuevo
		assertThat(listado.titulo()).isEqualTo(ListadoCursosPage.TITULO);
		assertThat(listado.numeroDeCursos()).isEqualTo(cursosAntes + 1);
		assertThat(listado.cursos())
				.contains(new FilaCurso("Selenium WebDriver", "Marta", "200,00 €", "242,00 €"));
	}

	@Test
	@DisplayName("Editar un curso cambia sus datos sin crear uno nuevo")
	void edicionDeCurso() {
		// Dado un curso guardado
		Curso curso = guardar(CURSO_ORIGINAL);
		ListadoCursosPage listado = abrirListado();
		int cursosAntes = listado.numeroDeCursos();

		// Cuando el usuario lo edita
		FormularioCursoPage formulario = listado.pulsarEditar(curso.getId());
		assertThat(formulario.titulo()).isEqualTo(FormularioCursoPage.TITULO_EDICION);
		assertThat(formulario.datosMostrados()).isEqualTo(CURSO_ORIGINAL);
		listado = formulario.rellenar(CURSO_EDITADO).guardar();

		// Entonces el listado muestra los datos nuevos y no hay cursos de más
		assertThat(listado.titulo()).isEqualTo(ListadoCursosPage.TITULO);
		assertThat(listado.numeroDeCursos()).isEqualTo(cursosAntes);
		assertThat(listado.cursoConId(curso.getId()))
				.isEqualTo(new FilaCurso("Curso editado con Selenium", "Lucía", "150,00 €", "181,50 €"));
		assertThat(cursoRepository.findById(curso.getId())).get()
				.extracting(Curso::getTitulo, Curso::getAutor, Curso::getPrecio)
				.containsExactly("Curso editado con Selenium", "Lucía", 150.0);
	}

	@Test
	@DisplayName("Borrar un curso, tras confirmarlo, lo quita del listado")
	void borradoDeCurso() {
		// Dado un curso guardado
		Curso curso = guardar(CURSO_A_BORRAR);
		ListadoCursosPage listado = abrirListado();
		int cursosAntes = listado.numeroDeCursos();

		// Cuando el usuario pulsa "Borrar" y acepta la confirmación
		ConfirmacionBorrado confirmacion = listado.pulsarBorrar(curso.getId());
		assertThat(confirmacion.mensaje()).isEqualTo("¿Seguro que quieres borrar este curso?");
		listado = confirmacion.aceptar();

		// Entonces vuelve al listado sin ese curso
		assertThat(listado.titulo()).isEqualTo(ListadoCursosPage.TITULO);
		assertThat(listado.numeroDeCursos()).isEqualTo(cursosAntes - 1);
		assertThat(listado.cursos())
				.extracting(FilaCurso::titulo)
				.doesNotContain(CURSO_A_BORRAR.titulo());
		assertThat(cursoRepository.findById(curso.getId())).isEmpty();
	}

	private Curso guardar(DatosCurso datos) {
		return cursoRepository.save(new Curso(datos.titulo(), datos.autor(), datos.precio()));
	}

	private ListadoCursosPage abrirListado() {
		return ListadoCursosPage.abrir(driver, urlBase());
	}
}
