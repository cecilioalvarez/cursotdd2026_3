package com.arquitecturajava.web1.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.web1.negocio.Curso;
import com.arquitecturajava.web1.negocio.Imparticion;
import com.arquitecturajava.web1.repositorios.CursoRepository;
import com.arquitecturajava.web1.repositorios.ImparticionRepository;

@Service
@Transactional(readOnly = true)
public class ImparticionService {

	private final ImparticionRepository imparticionRepository;
	private final CursoRepository cursoRepository;

	public ImparticionService(ImparticionRepository imparticionRepository, CursoRepository cursoRepository) {
		this.imparticionRepository = imparticionRepository;
		this.cursoRepository = cursoRepository;
	}

	public List<Imparticion> buscarPorCurso(Long cursoId) {
		return imparticionRepository.findByCursoIdOrderByFechaInicio(cursoId);
	}

	// El curso se carga dentro de la transacción para poder enlazarlo con la impartición
	@Transactional
	public Imparticion crear(Long cursoId, Imparticion imparticion) {
		Curso curso = cursoRepository.findById(cursoId)
				.orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + cursoId));
		curso.addImparticion(imparticion);
		return imparticionRepository.save(imparticion);
	}
}
