package com.arquitecturajava.web1.repositorios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ImparticionRepositoryTest {

	private static final LocalDate INICIO = LocalDate.of(2026, 11, 2);
	private static final LocalDate FIN = LocalDate.of(2026, 11, 6);

	@Autowired
	private ImparticionRepository imparticionRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Curso curso;

	@BeforeEach
	void prepararCurso() {
		curso = entityManager.persistAndFlush(new Curso("Java", "Cecilio", 100));
	}

	@Test
	void guardarAsignaIdALaImparticion() {
		Imparticion imparticion = new Imparticion("Noviembre", INICIO, FIN);
		curso.addImparticion(imparticion);

		Imparticion guardada = imparticionRepository.save(imparticion);

		assertThat(guardada.getId()).isNotNull();
	}

	@Test
	void buscarPorIdDevuelveLaImparticionConSuCurso() {
		Imparticion imparticion = persistir(curso, "Noviembre", INICIO, FIN);
		entityManager.clear();

		Optional<Imparticion> encontrada = imparticionRepository.findById(imparticion.getId());

		assertThat(encontrada).isPresent();
		assertThat(encontrada.get().getNombre()).isEqualTo("Noviembre");
		assertThat(encontrada.get().getFechaInicio()).isEqualTo(INICIO);
		assertThat(encontrada.get().getFechaFin()).isEqualTo(FIN);
		assertThat(encontrada.get().getCurso()).isEqualTo(curso);
	}

	@Test
	void buscarPorIdInexistenteDevuelveVacio() {
		assertThat(imparticionRepository.findById(999L)).isEmpty();
	}

	@Test
	void buscarPorCursoDevuelveSoloLasDeEseCursoOrdenadasPorFechaDeInicio() {
		Curso otroCurso = entityManager.persist(new Curso("Spring", "Ana", 50));
		persistir(curso, "Diciembre", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5));
		persistir(curso, "Noviembre", INICIO, FIN);
		persistir(otroCurso, "Spring noviembre", INICIO, FIN);
		entityManager.clear();

		assertThat(imparticionRepository.findByCursoIdOrderByFechaInicio(curso.getId()))
				.extracting(Imparticion::getNombre)
				.containsExactly("Noviembre", "Diciembre");
	}

	@Test
	void buscarPorCursoSinImparticionesDevuelveListaVacia() {
		assertThat(imparticionRepository.findByCursoIdOrderByFechaInicio(curso.getId())).isEmpty();
	}

	@Test
	void actualizarModificaLaImparticion() {
		Imparticion imparticion = persistir(curso, "Noviembre", INICIO, FIN);

		imparticion.setNombre("Noviembre (aplazada)");
		imparticion.setFechaFin(LocalDate.of(2026, 11, 13));
		imparticionRepository.saveAndFlush(imparticion);
		entityManager.clear();

		assertThat(imparticionRepository.findById(imparticion.getId()))
				.get()
				.extracting(Imparticion::getNombre, Imparticion::getFechaFin)
				.containsExactly("Noviembre (aplazada)", LocalDate.of(2026, 11, 13));
	}

	@Test
	void borrarEliminaLaImparticionPeroNoElCurso() {
		Imparticion imparticion = persistir(curso, "Noviembre", INICIO, FIN);
		entityManager.clear();

		imparticionRepository.deleteById(imparticion.getId());
		imparticionRepository.flush();

		assertThat(imparticionRepository.findById(imparticion.getId())).isEmpty();
		assertThat(entityManager.find(Curso.class, curso.getId())).isNotNull();
	}

	@Test
	void guardarSinCursoFalla() {
		Imparticion huerfana = new Imparticion("Noviembre", INICIO, FIN);

		assertThatThrownBy(() -> imparticionRepository.saveAndFlush(huerfana))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void guardarConFechaFinAnteriorAInicioFalla() {
		Imparticion imparticion = new Imparticion("Noviembre", FIN, INICIO);
		curso.addImparticion(imparticion);

		assertThatThrownBy(() -> imparticionRepository.saveAndFlush(imparticion))
				.isInstanceOf(ConstraintViolationException.class)
				.hasMessageContaining("La fecha de fin no puede ser anterior a la de inicio");
	}

	private Imparticion persistir(Curso cursoDeLaImparticion, String nombre, LocalDate inicio, LocalDate fin) {
		Imparticion imparticion = new Imparticion(nombre, inicio, fin);
		cursoDeLaImparticion.addImparticion(imparticion);
		return entityManager.persistAndFlush(imparticion);
	}
}
