package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.PreguntaActividad;

/**
 * Acceso a datos de la relacion N:M {@link PreguntaActividad}.
 */
public interface PreguntaActividadRepository extends JpaRepository<PreguntaActividad, Long> {

    List<PreguntaActividad> findAllByActividadId(Long actividadId);

    List<PreguntaActividad> findAllByPreguntaId(Long preguntaId);

    boolean existsByActividadIdAndPreguntaId(Long actividadId, Long preguntaId);
}
