package com.arquitecturajava.web1.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.servicios.CursoService;

@Controller
@RequestMapping("/cursos")
public class CursoController {

	private final CursoService cursoService;

	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	// El id lo genera la base de datos: no se acepta desde el formulario
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
	public String crear(@ModelAttribute("curso") Curso curso, BindingResult resultado) {
		if (resultado.hasErrors()) {
			return "cursos/formulario";
		}
		cursoService.guardar(curso);
		return "redirect:/cursos";
	}
}
