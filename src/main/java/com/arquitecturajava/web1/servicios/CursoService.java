package com.arquitecturajava.web1.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.repositorios.CursoRepository;

@Service
@Transactional(readOnly = true)
public class CursoService {

	private final CursoRepository cursoRepository;

	public CursoService(CursoRepository cursoRepository) {
		this.cursoRepository = cursoRepository;
	}

	public List<Curso> buscarTodos() {
		return cursoRepository.findAll();
	}

	public Optional<Curso> buscarPorId(Long id) {
		return cursoRepository.findById(id);
	}

	@Transactional
	public Curso guardar(Curso curso) {
		return cursoRepository.save(curso);
	}

	@Transactional
	public void borrar(Long id) {
		cursoRepository.deleteById(id);
	}
}
