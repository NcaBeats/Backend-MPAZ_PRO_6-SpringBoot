package com.example.mpaz_pro_6springboot.evaluacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;

/**
 * Acceso a datos de {@link Intento}.
 */
public interface IntentoRepository extends JpaRepository<Intento, Long> {

    List<Intento> findAllByUsuarioId(Long usuarioId);

    List<Intento> findAllByActividadCursoId(Long actividadCursoId);

    List<Intento> findAllByUsuarioIdAndActividadCursoId(Long usuarioId, Long actividadCursoId);

    List<Intento> findAllByActividadCursoActividadId(Long actividadId);

    List<Intento> findAllByUsuarioIdAndActividadCursoActividadId(Long usuarioId, Long actividadId);

    long countByUsuarioIdAndActividadCursoId(Long usuarioId, Long actividadCursoId);
}
