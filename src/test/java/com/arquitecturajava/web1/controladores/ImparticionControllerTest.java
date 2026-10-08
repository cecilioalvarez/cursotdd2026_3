package com.arquitecturajava.web1.controladores;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;
import com.arquitecturajava.web1.servicios.CursoService;
import com.arquitecturajava.web1.servicios.ImparticionService;

@WebMvcTest(ImparticionController.class)
class ImparticionControllerTest {

	private static final Curso CURSO = new Curso(1L, "Java desde cero", "Cecilio", 100);

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CursoService cursoService;

	@MockitoBean
	private ImparticionService imparticionService;

	@BeforeEach
	void cursoExistente() {
		when(cursoService.buscarPorId(1L)).thenReturn(Optional.of(CURSO));
	}

	@Test
	void listarMuestraLasImparticionesDelCurso() throws Exception {
		List<Imparticion> imparticiones = List.of(
				new Imparticion(1L, "Noviembre 2026", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6)),
				new Imparticion(2L, "Diciembre 2026", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5)));
		when(imparticionService.buscarPorCurso(1L)).thenReturn(imparticiones);

		mockMvc.perform(get("/cursos/1/imparticiones"))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/lista"))
				.andExpect(model().attribute("curso", CURSO))
				.andExpect(model().attribute("imparticiones", imparticiones))
				.andExpect(content().string(containsString("Java desde cero")))
				.andExpect(content().string(containsString("Noviembre 2026")))
				.andExpect(content().string(containsString("02/11/2026")))
				.andExpect(content().string(containsString("06/11/2026")))
				.andExpect(content().string(containsString("Diciembre 2026")))
				.andExpect(content().string(not(containsString("Este curso no tiene imparticiones."))));
	}

	@Test
	void listarSinImparticionesMuestraAviso() throws Exception {
		when(imparticionService.buscarPorCurso(1L)).thenReturn(List.of());

		mockMvc.perform(get("/cursos/1/imparticiones"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Este curso no tiene imparticiones.")))
				.andExpect(content().string(not(containsString("<table"))));
	}

	@Test
	void listarTieneEnlacesANuevaImparticionYAlListadoDeCursos() throws Exception {
		when(imparticionService.buscarPorCurso(1L)).thenReturn(List.of());

		mockMvc.perform(get("/cursos/1/imparticiones"))
				.andExpect(content().string(containsString("href=\"/cursos/1/imparticiones/nueva\"")))
				.andExpect(content().string(containsString("href=\"/cursos\"")));
	}

	@Test
	void listarDeCursoInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get("/cursos/999/imparticiones"))
				.andExpect(status().isNotFound());

		verify(imparticionService, never()).buscarPorCurso(anyLong());
	}

	@Test
	void formularioNuevaMuestraUnaImparticionVaciaDelCurso() throws Exception {
		mockMvc.perform(get("/cursos/1/imparticiones/nueva"))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/formulario"))
				.andExpect(model().attributeExists("imparticion"))
				.andExpect(content().string(containsString("Java desde cero")))
				.andExpect(content().string(containsString("action=\"/cursos/1/imparticiones\"")))
				.andExpect(content().string(containsString("type=\"date\"")));
	}

	@Test
	void formularioNuevaDeCursoInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get("/cursos/999/imparticiones/nueva"))
				.andExpect(status().isNotFound());
	}

	@Test
	void crearGuardaLaImparticionYRedirigeAlListado() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("nombre", "Noviembre 2026")
				.param("fechaInicio", "2026-11-02")
				.param("fechaFin", "2026-11-06"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cursos/1/imparticiones"));

		verify(imparticionService).crear(eq(1L), argThat(imparticion -> imparticion.getId() == null
				&& imparticion.getNombre().equals("Noviembre 2026")
				&& imparticion.getFechaInicio().equals(LocalDate.of(2026, 11, 2))
				&& imparticion.getFechaFin().equals(LocalDate.of(2026, 11, 6))));
	}

	@Test
	void crearIgnoraElIdYElCursoEnviadosDesdeElFormulario() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("id", "7")
				.param("curso.id", "2")
				.param("nombre", "Noviembre 2026")
				.param("fechaInicio", "2026-11-02")
				.param("fechaFin", "2026-11-06"))
				.andExpect(redirectedUrl("/cursos/1/imparticiones"));

		verify(imparticionService).crear(eq(1L),
				argThat(imparticion -> imparticion.getId() == null && imparticion.getCurso() == null));
	}

	@Test
	void crearConNombreVacioVuelveAlFormularioSinGuardar() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("nombre", " ")
				.param("fechaInicio", "2026-11-02")
				.param("fechaFin", "2026-11-06"))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("imparticion", "nombre", "NotBlank"))
				.andExpect(content().string(containsString("El nombre es obligatorio")));

		verify(imparticionService, never()).crear(anyLong(), any());
	}

	@Test
	void crearSinFechasVuelveAlFormularioSinGuardar() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("nombre", "Noviembre 2026")
				.param("fechaInicio", "")
				.param("fechaFin", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("imparticion", "fechaInicio", "NotNull"))
				.andExpect(model().attributeHasFieldErrorCode("imparticion", "fechaFin", "NotNull"));

		verify(imparticionService, never()).crear(anyLong(), any());
	}

	@Test
	void crearConFechaIncorrectaVuelveAlFormularioSinGuardar() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("nombre", "Noviembre 2026")
				.param("fechaInicio", "abc")
				.param("fechaFin", "2026-11-06"))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/formulario"))
				.andExpect(model().attributeHasFieldErrors("imparticion", "fechaInicio"));

		verify(imparticionService, never()).crear(anyLong(), any());
	}

	@Test
	void crearConFechaFinAnteriorAInicioVuelveAlFormularioSinGuardar() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("nombre", "Noviembre 2026")
				.param("fechaInicio", "2026-11-06")
				.param("fechaFin", "2026-11-02"))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("imparticion", "fechasEnOrden", "AssertTrue"))
				.andExpect(content().string(containsString("La fecha de fin no puede ser anterior a la de inicio")))
				// Las fechas que escribió el usuario se conservan en el formulario
				.andExpect(content().string(containsString("value=\"2026-11-06\"")));

		verify(imparticionService, never()).crear(anyLong(), any());
	}

	@Test
	void crearEnCursoInexistenteDevuelve404SinGuardar() throws Exception {
		mockMvc.perform(post("/cursos/999/imparticiones")
				.param("nombre", "Noviembre 2026")
				.param("fechaInicio", "2026-11-02")
				.param("fechaFin", "2026-11-06"))
				.andExpect(status().isNotFound());

		verify(imparticionService, never()).crear(anyLong(), any());
	}
}
