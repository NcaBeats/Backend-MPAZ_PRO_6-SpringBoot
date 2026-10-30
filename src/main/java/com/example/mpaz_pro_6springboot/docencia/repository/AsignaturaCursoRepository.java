package com.example.mpaz_pro_6springboot.docencia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.docencia.model.AsignaturaCurso;

/**
 * Acceso a datos de la relacion {@link AsignaturaCurso}.
 */
public interface AsignaturaCursoRepository extends JpaRepository<AsignaturaCurso, Long> {

    List<AsignaturaCurso> findAllByCursoId(Long cursoId);

    List<AsignaturaCurso> findAllByAsignaturaId(Long asignaturaId);

    boolean existsByCursoIdAndAsignaturaId(Long cursoId, Long asignaturaId);
}
