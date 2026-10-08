package com.arquitecturajava.web1.repositorios;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class CursoRepositoryTest {

	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void guardarAsignaIdAlCurso() {
		Curso guardado = cursoRepository.save(new Curso("Java", "Cecilio", 100));

		assertThat(guardado.getId()).isNotNull();
	}

	@Test
	void buscarPorIdDevuelveElCurso() {
		Curso curso = entityManager.persistAndFlush(new Curso("Java", "Cecilio", 100));

		Optional<Curso> encontrado = cursoRepository.findById(curso.getId());

		assertThat(encontrado).isPresent();
		assertThat(encontrado.get().getTitulo()).isEqualTo("Java");
		assertThat(encontrado.get().getAutor()).isEqualTo("Cecilio");
		assertThat(encontrado.get().getPrecio()).isEqualTo(100);
	}

	@Test
	void buscarPorIdInexistenteDevuelveVacio() {
		assertThat(cursoRepository.findById(999L)).isEmpty();
	}

	@Test
	void buscarTodosDevuelveTodosLosCursos() {
		entityManager.persist(new Curso("Java", "Cecilio", 100));
		entityManager.persist(new Curso("Spring", "Ana", 50));
		entityManager.flush();

		List<Curso> cursos = cursoRepository.findAll();

		assertThat(cursos).hasSize(2)
				.extracting(Curso::getTitulo)
				.containsExactlyInAnyOrder("Java", "Spring");
	}

	@Test
	void actualizarModificaElCurso() {
		Curso curso = entityManager.persistAndFlush(new Curso("Java", "Cecilio", 100));

		curso.setPrecio(80);
		cursoRepository.saveAndFlush(curso);
		entityManager.clear();

		assertThat(cursoRepository.findById(curso.getId()))
				.get()
				.extracting(Curso::getPrecio)
				.isEqualTo(80.0);
	}

	@Test
	void cursoSeRecuperaConSusImparticionesOrdenadasPorFechaDeInicio() {
		Curso curso = entityManager.persist(new Curso("Java", "Cecilio", 100));
		Imparticion diciembre = new Imparticion("Diciembre", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5));
		Imparticion noviembre = new Imparticion("Noviembre", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6));
		curso.addImparticion(diciembre);
		curso.addImparticion(noviembre);
		entityManager.persist(diciembre);
		entityManager.persist(noviembre);
		entityManager.flush();
		entityManager.clear();

		Curso recuperado = cursoRepository.findById(curso.getId()).orElseThrow();

		assertThat(recuperado.getImparticiones())
				.extracting(Imparticion::getNombre)
				.containsExactly("Noviembre", "Diciembre");
	}

	@Test
	void editarElCursoSinImparticionesNoBorraLasQueTiene() {
		Curso curso = persistirCursoConImparticion();

		// Como en el formulario de edición: un Curso nuevo con el mismo id y sin imparticiones
		cursoRepository.saveAndFlush(new Curso(curso.getId(), "Java avanzado", "Cecilio", 150));
		entityManager.clear();

		assertThat(entityManager.getEntityManager()
				.createQuery("select count(i) from Imparticion i", Long.class)
				.getSingleResult()).isEqualTo(1);
	}

	@Test
	void borrarElCursoBorraSusImparticiones() {
		Curso curso = persistirCursoConImparticion();

		cursoRepository.deleteById(curso.getId());
		cursoRepository.flush();

		assertThat(entityManager.getEntityManager()
				.createQuery("select count(i) from Imparticion i", Long.class)
				.getSingleResult()).isZero();
	}

	private Curso persistirCursoConImparticion() {
		Curso curso = entityManager.persist(new Curso("Java", "Cecilio", 100));
		Imparticion imparticion = new Imparticion("Noviembre", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6));
		curso.addImparticion(imparticion);
		entityManager.persist(imparticion);
		entityManager.flush();
		entityManager.clear();
		return curso;
	}

	@Test
	void borrarEliminaElCurso() {
		Curso curso = entityManager.persistAndFlush(new Curso("Java", "Cecilio", 100));

		cursoRepository.deleteById(curso.getId());
		cursoRepository.flush();

		assertThat(cursoRepository.findById(curso.getId())).isEmpty();
	}
}
