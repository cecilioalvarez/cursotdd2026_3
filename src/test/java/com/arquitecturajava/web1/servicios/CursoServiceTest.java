package com.arquitecturajava.web1.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.repositorios.CursoRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

	@Mock
	private CursoRepository cursoRepository;

	@InjectMocks
	private CursoService cursoService;

	@Test
	void buscarTodosDevuelveLosCursosDelRepositorio() {
		List<Curso> cursos = List.of(
				new Curso(1L, "Java", "Cecilio", 100),
				new Curso(2L, "Spring", "Ana", 50));
		when(cursoRepository.findAll()).thenReturn(cursos);

		assertThat(cursoService.buscarTodos()).isEqualTo(cursos);
	}

	@Test
	void buscarPorIdDevuelveElCursoSiExiste() {
		Curso curso = new Curso(1L, "Java", "Cecilio", 100);
		when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

		assertThat(cursoService.buscarPorId(1L)).contains(curso);
	}

	@Test
	void buscarPorIdDevuelveVacioSiNoExiste() {
		when(cursoRepository.findById(999L)).thenReturn(Optional.empty());

		assertThat(cursoService.buscarPorId(999L)).isEmpty();
	}

	@Test
	void guardarDevuelveElCursoGuardado() {
		Curso nuevo = new Curso("Java", "Cecilio", 100);
		Curso guardado = new Curso(1L, "Java", "Cecilio", 100);
		when(cursoRepository.save(nuevo)).thenReturn(guardado);

		assertThat(cursoService.guardar(nuevo)).isEqualTo(guardado);
		verify(cursoRepository).save(nuevo);
	}

	@Test
	void borrarLlamaAlRepositorio() {
		cursoService.borrar(1L);

		verify(cursoRepository).deleteById(1L);
	}
}
