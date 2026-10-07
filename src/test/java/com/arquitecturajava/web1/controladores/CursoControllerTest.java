package com.arquitecturajava.web1.controladores;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.servicios.CursoService;

@WebMvcTest(CursoController.class)
class CursoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CursoService cursoService;

	@Test
	void listarMuestraLaVistaConLosCursos() throws Exception {
		List<Curso> cursos = List.of(
				new Curso(1L, "Java", "Cecilio", 100),
				new Curso(2L, "Spring", "Ana", 19.99));
		when(cursoService.buscarTodos()).thenReturn(cursos);

		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/lista"))
				.andExpect(model().attribute("cursos", cursos));

		verify(cursoService).buscarTodos();
	}

	@Test
	void listarPintaLosCursosEnLaTabla() throws Exception {
		when(cursoService.buscarTodos()).thenReturn(List.of(
				new Curso(1L, "Java", "Cecilio", 100),
				new Curso(2L, "Spring", "Ana", 19.99)));

		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Java")))
				.andExpect(content().string(containsString("Cecilio")))
				.andExpect(content().string(containsString("100,00 €")))
				.andExpect(content().string(containsString("121,00 €")))
				.andExpect(content().string(containsString("Spring")))
				.andExpect(content().string(containsString("19,99 €")))
				.andExpect(content().string(containsString("24,19 €")))
				.andExpect(content().string(not(containsString("No hay cursos."))));
	}

	@Test
	void listarSinCursosMuestraAviso() throws Exception {
		when(cursoService.buscarTodos()).thenReturn(List.of());

		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("No hay cursos.")))
				.andExpect(content().string(not(containsString("<table"))));
	}

	@Test
	void listarCargaBootstrapDesdeCdn() throws Exception {
		when(cursoService.buscarTodos()).thenReturn(List.of());

		mockMvc.perform(get("/cursos"))
				.andExpect(content().string(containsString("cdn.jsdelivr.net/npm/bootstrap")));
	}
}
