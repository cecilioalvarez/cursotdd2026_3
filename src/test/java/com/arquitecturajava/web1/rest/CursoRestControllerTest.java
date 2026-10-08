package com.arquitecturajava.web1.rest;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;
import com.arquitecturajava.web1.servicios.CursoService;

import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;

/**
 * Pruebas del servicio REST de cursos con REST Assured sobre MockMvc: no
 * arranca servidor ni base de datos, el servicio está simulado.
 */
@WebMvcTest(CursoRestController.class)
class CursoRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CursoService cursoService;

	@BeforeEach
	void configurarRestAssured() {
		RestAssuredMockMvc.mockMvc(mockMvc);
		// Por defecto los decimales del JSON se leen como float (24.19f != 24.19)
		RestAssuredMockMvc.config = RestAssuredMockMvcConfig.config()
				.jsonConfig(JsonConfig.jsonConfig().numberReturnType(NumberReturnType.DOUBLE));
	}

	@AfterEach
	void limpiarRestAssured() {
		RestAssuredMockMvc.reset();
	}

	@Test
	void listarDevuelveLosCursosEnJson() {
		when(cursoService.buscarTodos()).thenReturn(List.of(
				new Curso(1L, "Java", "Cecilio", 100),
				new Curso(2L, "Spring", "Ana", 19.99)));

		given()
		.when()
				.get("/api/cursos")
		.then()
				.statusCode(200)
				.contentType(ContentType.JSON)
				.body("titulo", contains("Java", "Spring"))
				.body("[0].id", equalTo(1))
				.body("[0].autor", equalTo("Cecilio"))
				.body("[0].precio", equalTo(100.0))
				.body("[0].precioConIva", equalTo(121.0))
				.body("[1].autor", equalTo("Ana"))
				.body("[1].precioConIva", equalTo(24.19));
	}

	@Test
	void listarSinCursosDevuelveListaVacia() {
		when(cursoService.buscarTodos()).thenReturn(List.of());

		given()
		.when()
				.get("/api/cursos")
		.then()
				.statusCode(200)
				.body("", empty());
	}

	@Test
	void listarNoExponeLasImparticiones() {
		Curso curso = new Curso(1L, "Java", "Cecilio", 100);
		curso.addImparticion(new Imparticion("Noviembre", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6)));
		when(cursoService.buscarTodos()).thenReturn(List.of(curso));

		given()
		.when()
				.get("/api/cursos")
		.then()
				.statusCode(200)
				.body("[0].imparticiones", nullValue());
	}

	@Test
	void crearGuardaElCursoYDevuelve201ConElCursoCreado() {
		when(cursoService.guardar(any())).thenReturn(new Curso(5L, "Kotlin", "Marta", 200));

		given()
				.contentType(ContentType.JSON)
				.body("""
						{"titulo": "Kotlin", "autor": "Marta", "precio": 200}
						""")
		.when()
				.post("/api/cursos")
		.then()
				.statusCode(201)
				.body("id", equalTo(5))
				.body("titulo", equalTo("Kotlin"))
				.body("autor", equalTo("Marta"))
				.body("precio", equalTo(200.0))
				.body("precioConIva", equalTo(242.0));

		verify(cursoService).guardar(argThat(curso -> curso.getId() == null
				&& curso.getTitulo().equals("Kotlin")
				&& curso.getAutor().equals("Marta")
				&& curso.getPrecio() == 200));
	}

	@Test
	void crearIgnoraElIdEnviadoEnElJson() {
		when(cursoService.guardar(any())).thenReturn(new Curso(5L, "Kotlin", "Marta", 200));

		given()
				.contentType(ContentType.JSON)
				.body("""
						{"id": 1, "titulo": "Kotlin", "autor": "Marta", "precio": 200}
						""")
		.when()
				.post("/api/cursos")
		.then()
				.statusCode(201);

		verify(cursoService).guardar(argThat(curso -> curso.getId() == null));
	}

	@Test
	void crearConTituloVacioDevuelve400SinGuardar() {
		given()
				.contentType(ContentType.JSON)
				.body("""
						{"titulo": " ", "autor": "Marta", "precio": 200}
						""")
		.when()
				.post("/api/cursos")
		.then()
				.statusCode(400)
				.body("titulo", equalTo("El título es obligatorio"))
				.body("autor", nullValue());

		verify(cursoService, never()).guardar(any());
	}

	@Test
	void crearSinTituloNiAutorDevuelve400ConLosDosErrores() {
		given()
				.contentType(ContentType.JSON)
				.body("""
						{"precio": 200}
						""")
		.when()
				.post("/api/cursos")
		.then()
				.statusCode(400)
				.body("titulo", equalTo("El título es obligatorio"))
				.body("autor", equalTo("El autor es obligatorio"));

		verify(cursoService, never()).guardar(any());
	}

	@Test
	void crearConJsonMalFormadoDevuelve400SinGuardar() {
		given()
				.contentType(ContentType.JSON)
				.body("{\"titulo\": ")
		.when()
				.post("/api/cursos")
		.then()
				.statusCode(400);

		verify(cursoService, never()).guardar(any());
	}
}
