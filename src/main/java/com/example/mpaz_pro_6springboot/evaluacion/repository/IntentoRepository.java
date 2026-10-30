package com.example.mpaz_pro_6springboot.evaluacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;

/**
 * Acceso a datos de {@link Intento}.
 */
public interface IntentoRepository extends JpaRepository<Intento, Long> {

    List<Intento> findAllByUsuarioId(Long usuarioId);

    List<Intento> findAllByActividadId(Long actividadId);

    List<Intento> findAllByUsuarioIdAndActividadId(Long usuarioId, Long actividadId);

    long countByUsuarioIdAndActividadId(Long usuarioId, Long actividadId);
}
