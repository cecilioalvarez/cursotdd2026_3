package com.arquitecturajava.web1.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;
import com.arquitecturajava.web1.repositorios.CursoRepository;
import com.arquitecturajava.web1.repositorios.ImparticionRepository;

@ExtendWith(MockitoExtension.class)
class ImparticionServiceTest {

	private static final LocalDate INICIO = LocalDate.of(2026, 11, 2);
	private static final LocalDate FIN = LocalDate.of(2026, 11, 6);

	@Mock
	private ImparticionRepository imparticionRepository;

	@Mock
	private CursoRepository cursoRepository;

	@InjectMocks
	private ImparticionService imparticionService;

	@Test
	void buscarPorCursoDevuelveLasImparticionesDelRepositorio() {
		List<Imparticion> imparticiones = List.of(
				new Imparticion(1L, "Noviembre", INICIO, FIN),
				new Imparticion(2L, "Diciembre", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5)));
		when(imparticionRepository.findByCursoIdOrderByFechaInicio(1L)).thenReturn(imparticiones);

		assertThat(imparticionService.buscarPorCurso(1L)).isEqualTo(imparticiones);
	}

	@Test
	void crearEnlazaLaImparticionConSuCursoYLaGuarda() {
		Curso curso = new Curso(1L, "Java", "Cecilio", 100);
		Imparticion nueva = new Imparticion("Noviembre", INICIO, FIN);
		Imparticion guardada = new Imparticion(10L, "Noviembre", INICIO, FIN);
		when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
		when(imparticionRepository.save(nueva)).thenReturn(guardada);

		assertThat(imparticionService.crear(1L, nueva)).isEqualTo(guardada);
		assertThat(nueva.getCurso()).isSameAs(curso);
		assertThat(curso.getImparticiones()).containsExactly(nueva);
		verify(imparticionRepository).save(nueva);
	}

	@Test
	void crearEnCursoInexistenteFallaSinGuardar() {
		when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> imparticionService.crear(999L, new Imparticion("Noviembre", INICIO, FIN)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Curso no encontrado: 999");
		verify(imparticionRepository, never()).save(any());
	}
}
