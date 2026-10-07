package com.arquitecturajava.web1.integracion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.repositorios.CursoRepository;

/**
 * Flujo completo: petición HTTP -> CursoController -> CursoService ->
 * CursoRepository -> H2, y vuelta hasta la vista Thymeleaf. Cada test se
 * ejecuta en una transacción que se deshace al terminar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CursoIntegracionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CursoRepository cursoRepository;

	@BeforeEach
	void prepararDatos() {
		cursoRepository.deleteAll();
	}

	@Test
	void listarMuestraLosCursosGuardadosEnLaBaseDeDatos() throws Exception {
		cursoRepository.save(new Curso("Java", "Cecilio", 100));
		cursoRepository.save(new Curso("Spring", "Ana", 19.99));

		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/lista"))
				.andExpect(model().attribute("cursos", hasSize(2)))
				.andExpect(content().string(containsString("Java")))
				.andExpect(content().string(containsString("Cecilio")))
				.andExpect(content().string(containsString("121,00 €")))
				.andExpect(content().string(containsString("Spring")))
				.andExpect(content().string(containsString("Ana")))
				.andExpect(content().string(containsString("24,19 €")));
	}

	@Test
	void listarSinCursosEnLaBaseDeDatosMuestraAviso() throws Exception {
		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("cursos", hasSize(0)))
				.andExpect(content().string(containsString("No hay cursos.")));
	}

	@Test
	void cursoBorradoDelRepositorioNoApareceEnElListado() throws Exception {
		cursoRepository.save(new Curso("Java", "Cecilio", 100));
		Curso borrado = cursoRepository.save(new Curso("Spring", "Ana", 50));

		cursoRepository.deleteById(borrado.getId());

		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("cursos", hasSize(1)))
				.andExpect(content().string(containsString("Java")))
				.andExpect(content().string(not(containsString("Spring"))));
	}

	@Test
	void crearCursoDesdeElFormularioLoGuardaYApareceEnElListado() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("titulo", "Kotlin")
				.param("autor", "Marta")
				.param("precio", "200"))
				.andExpect(redirectedUrl("/cursos"));

		assertThat(cursoRepository.findAll())
				.singleElement()
				.satisfies(curso -> {
					assertThat(curso.getTitulo()).isEqualTo("Kotlin");
					assertThat(curso.getAutor()).isEqualTo("Marta");
					assertThat(curso.getPrecio()).isEqualTo(200);
				});

		mockMvc.perform(get("/cursos"))
				.andExpect(content().string(containsString("Kotlin")))
				.andExpect(content().string(containsString("242,00 €")));
	}
}
