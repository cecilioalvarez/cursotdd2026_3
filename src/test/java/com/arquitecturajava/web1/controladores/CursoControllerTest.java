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
import java.util.Optional;

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
	void crearIgnoraLasImparticionesEnviadasDesdeElFormulario() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("titulo", "Java")
				.param("autor", "Cecilio")
				.param("precio", "100")
				.param("imparticiones[0].nombre", "Colada"))
				.andExpect(redirectedUrl("/cursos"));

		verify(cursoService).guardar(argThat(curso -> curso.getImparticiones().isEmpty()));
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

	@Test
	void formularioEditarMuestraElCursoExistente() throws Exception {
		Curso curso = new Curso(1L, "Java", "Cecilio", 100);
		when(cursoService.buscarPorId(1L)).thenReturn(Optional.of(curso));

		mockMvc.perform(get("/cursos/1/editar"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/formulario"))
				.andExpect(model().attribute("curso", curso))
				.andExpect(content().string(containsString("Editar curso")))
				.andExpect(content().string(containsString("action=\"/cursos/1\"")))
				.andExpect(content().string(containsString("value=\"Java\"")));
	}

	@Test
	void formularioEditarCursoInexistenteDevuelve404() throws Exception {
		when(cursoService.buscarPorId(999L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/cursos/999/editar"))
				.andExpect(status().isNotFound());
	}

	@Test
	void actualizarGuardaElCursoConElIdDeLaUrlYRedirige() throws Exception {
		when(cursoService.buscarPorId(1L)).thenReturn(Optional.of(new Curso(1L, "Java", "Cecilio", 100)));

		mockMvc.perform(post("/cursos/1")
				.param("id", "2")
				.param("titulo", "Java avanzado")
				.param("autor", "Cecilio")
				.param("precio", "150"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cursos"));

		verify(cursoService).guardar(argThat(curso -> curso.getId() == 1L
				&& curso.getTitulo().equals("Java avanzado")
				&& curso.getAutor().equals("Cecilio")
				&& curso.getPrecio() == 150));
	}

	@Test
	void actualizarConErroresVuelveAlFormularioDeEdicionSinGuardar() throws Exception {
		when(cursoService.buscarPorId(1L)).thenReturn(Optional.of(new Curso(1L, "Java", "Cecilio", 100)));

		mockMvc.perform(post("/cursos/1")
				.param("titulo", "")
				.param("autor", "Cecilio")
				.param("precio", "100"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("curso", "titulo", "NotBlank"))
				.andExpect(content().string(containsString("action=\"/cursos/1\"")));

		verify(cursoService, never()).guardar(any());
	}

	@Test
	void actualizarCursoInexistenteDevuelve404SinGuardar() throws Exception {
		when(cursoService.buscarPorId(999L)).thenReturn(Optional.empty());

		mockMvc.perform(post("/cursos/999")
				.param("titulo", "Java")
				.param("autor", "Cecilio")
				.param("precio", "100"))
				.andExpect(status().isNotFound());

		verify(cursoService, never()).guardar(any());
	}

	@Test
	void borrarEliminaElCursoYRedirigeAlListado() throws Exception {
		mockMvc.perform(post("/cursos/1/borrar"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cursos"));

		verify(cursoService).borrar(1L);
	}

	@Test
	void listarMuestraLosBotonesDeEditarYBorrar() throws Exception {
		when(cursoService.buscarTodos()).thenReturn(List.of(new Curso(1L, "Java", "Cecilio", 100)));

		mockMvc.perform(get("/cursos"))
				.andExpect(content().string(containsString("href=\"/cursos/1/editar\"")))
				.andExpect(content().string(containsString("action=\"/cursos/1/borrar\"")));
	}

	@Test
	void listarMuestraElBotonDeVerImparticiones() throws Exception {
		when(cursoService.buscarTodos()).thenReturn(List.of(new Curso(1L, "Java", "Cecilio", 100)));

		mockMvc.perform(get("/cursos"))
				.andExpect(content().string(containsString("href=\"/cursos/1/imparticiones\"")))
				.andExpect(content().string(containsString("Ver imparticiones")));
	}
}
