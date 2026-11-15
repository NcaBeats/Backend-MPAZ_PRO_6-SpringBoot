package com.example.mpaz_pro_6springboot.docencia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.docencia.model.ActividadCurso;

/**
 * Acceso a datos de {@link ActividadCurso}.
 */
public interface ActividadCursoRepository extends JpaRepository<ActividadCurso, Long> {

    List<ActividadCurso> findAllByCursoId(Long cursoId);

    List<ActividadCurso> findAllByActividadId(Long actividadId);

    boolean existsByActividadIdAndCursoId(Long actividadId, Long cursoId);
}
