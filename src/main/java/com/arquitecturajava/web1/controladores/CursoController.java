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
import com.arquitecturajava.web1.servicios.CursoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cursos")
public class CursoController {

	private final CursoService cursoService;

	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	// El id viene de la base de datos o de la URL: nunca del formulario
	@InitBinder
	void configurarBinder(WebDataBinder binder) {
		binder.setDisallowedFields("id");
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("cursos", cursoService.buscarTodos());
		return "cursos/lista";
	}

	@GetMapping("/nuevo")
	public String formularioNuevo(Model model) {
		model.addAttribute("curso", new Curso());
		return "cursos/formulario";
	}

	@PostMapping
	public String crear(@Valid @ModelAttribute("curso") Curso curso, BindingResult resultado) {
		if (resultado.hasErrors()) {
			return "cursos/formulario";
		}
		cursoService.guardar(curso);
		return "redirect:/cursos";
	}

	@GetMapping("/{id}/editar")
	public String formularioEditar(@PathVariable Long id, Model model) {
		model.addAttribute("curso", buscarOFallar(id));
		return "cursos/formulario";
	}

	@PostMapping("/{id}")
	public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("curso") Curso curso,
			BindingResult resultado) {
		buscarOFallar(id);
		curso.setId(id);
		if (resultado.hasErrors()) {
			return "cursos/formulario";
		}
		cursoService.guardar(curso);
		return "redirect:/cursos";
	}

	@PostMapping("/{id}/borrar")
	public String borrar(@PathVariable Long id) {
		cursoService.borrar(id);
		return "redirect:/cursos";
	}

	private Curso buscarOFallar(Long id) {
		return cursoService.buscarPorId(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado: " + id));
	}
}
