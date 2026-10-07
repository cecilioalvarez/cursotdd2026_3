package com.arquitecturajava.web1.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.web1.negocio.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

}
