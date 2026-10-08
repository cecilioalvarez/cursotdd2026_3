package com.arquitecturajava.web1.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;
import com.arquitecturajava.web1.servicios.CursoService;
import com.arquitecturajava.web1.servicios.ImparticionService;

import jakarta.validation.Valid;

/**
 * Imparticiones de un curso. Todas las rutas cuelgan del curso, así que si el
 * curso no existe cualquiera de ellas devuelve 404.
 */
@Controller
@RequestMapping("/cursos/{cursoId}/imparticiones")
public class ImparticionController {

	private final CursoService cursoService;
	private final ImparticionService imparticionService;

	public ImparticionController(CursoService cursoService, ImparticionService imparticionService) {
		this.cursoService = cursoService;
		this.imparticionService = imparticionService;
	}

	// El id lo pone la base de datos y el curso sale de la URL: nunca del formulario
	@InitBinder("imparticion")
	void configurarBinder(WebDataBinder binder) {
		binder.setDisallowedFields("id", "curso*");
	}

	// Se ejecuta antes de cada petición: deja el curso en el modelo para las vistas
	@ModelAttribute("curso")
	Curso curso(@PathVariable Long cursoId) {
		return cursoService.buscarPorId(cursoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado: " + cursoId));
	}

	@GetMapping
	public String listar(@PathVariable Long cursoId, Model model) {
		model.addAttribute("imparticiones", imparticionService.buscarPorCurso(cursoId));
		return "imparticiones/lista";
	}

	@GetMapping("/nueva")
	public String formularioNueva(Model model) {
		model.addAttribute("imparticion", new Imparticion());
		return "imparticiones/formulario";
	}

	@PostMapping
	public String crear(@PathVariable Long cursoId, @Valid @ModelAttribute("imparticion") Imparticion imparticion,
			BindingResult resultado) {
		if (resultado.hasErrors()) {
			return "imparticiones/formulario";
		}
		imparticionService.crear(cursoId, imparticion);
		return "redirect:/cursos/{cursoId}/imparticiones";
	}
}
