package com.arquitecturajava.web1.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.web1.negocio.Imparticion;

public interface ImparticionRepository extends JpaRepository<Imparticion, Long> {

	List<Imparticion> findByCursoIdOrderByFechaInicio(Long cursoId);
}
