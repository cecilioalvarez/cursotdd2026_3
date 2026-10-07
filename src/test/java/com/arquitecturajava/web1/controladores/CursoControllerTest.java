package com.arquitecturajava.web1.controladores;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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

	@Test
	void formularioNuevoMuestraLaVistaConUnCursoVacio() throws Exception {
		mockMvc.perform(get("/cursos/nuevo"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/formulario"))
				.andExpect(model().attributeExists("curso"))
				.andExpect(content().string(containsString("<form")));
	}

	@Test
	void crearGuardaElCursoYRedirigeAlListado() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("titulo", "Java")
				.param("autor", "Cecilio")
				.param("precio", "100"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cursos"));

		verify(cursoService).guardar(argThat(curso -> curso.getId() == null
				&& curso.getTitulo().equals("Java")
				&& curso.getAutor().equals("Cecilio")
				&& curso.getPrecio() == 100));
	}

	@Test
	void crearIgnoraElIdEnviadoDesdeElFormulario() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("id", "1")
				.param("titulo", "Java")
				.param("autor", "Cecilio")
				.param("precio", "100"))
				.andExpect(redirectedUrl("/cursos"));

		verify(cursoService).guardar(argThat(curso -> curso.getId() == null));
	}

	@Test
	void crearConPrecioIncorrectoVuelveAlFormularioSinGuardar() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("titulo", "Java")
				.param("autor", "Cecilio")
				.param("precio", "abc"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/formulario"))
				.andExpect(model().attributeHasFieldErrors("curso", "precio"));

		verify(cursoService, never()).guardar(any());
	}

	@Test
	void crearConTituloVacioVuelveAlFormularioSinGuardar() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("titulo", "   ")
				.param("autor", "Cecilio")
				.param("precio", "100"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("curso", "titulo", "NotBlank"))
				.andExpect(content().string(containsString("El título es obligatorio")));

		verify(cursoService, never()).guardar(any());
	}

	@Test
	void crearConAutorVacioVuelveAlFormularioSinGuardar() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("titulo", "Java")
				.param("autor", "")
				.param("precio", "100"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("curso", "autor", "NotBlank"))
				.andExpect(content().string(containsString("El autor es obligatorio")));

		verify(cursoService, never()).guardar(any());
	}
}
