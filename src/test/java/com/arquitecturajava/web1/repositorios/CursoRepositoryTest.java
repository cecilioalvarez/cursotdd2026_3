package com.arquitecturajava.web1.repositorios;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.arquitecturajava.web1.negocio.Curso;

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
	void borrarEliminaElCurso() {
		Curso curso = entityManager.persistAndFlush(new Curso("Java", "Cecilio", 100));

		cursoRepository.deleteById(curso.getId());
		cursoRepository.flush();

		assertThat(cursoRepository.findById(curso.getId())).isEmpty();
	}
}
