package com.arquitecturajava.web1.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arquitecturajava.web1.servicios.CursoService;

@Controller
@RequestMapping("/cursos")
public class CursoController {

	private final CursoService cursoService;

	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("cursos", cursoService.buscarTodos());
		return "cursos/lista";
	}
}
