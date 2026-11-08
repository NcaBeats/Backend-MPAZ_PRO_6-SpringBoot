package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mpaz_pro_6springboot.curriculo.model.PreguntaActividad;

/**
 * Acceso a datos de la relacion N:M {@link PreguntaActividad}.
 */
public interface PreguntaActividadRepository extends JpaRepository<PreguntaActividad, Long> {

    List<PreguntaActividad> findAllByActividadId(Long actividadId);

    List<PreguntaActividad> findAllByPreguntaId(Long preguntaId);

    boolean existsByActividadIdAndPreguntaId(Long actividadId, Long preguntaId);

    /**
     * Carga las preguntas de un conjunto de actividades con su recurso y
     * contenido ya resueltos, para mapearlas a objetivos de aprendizaje sin
     * consultas perezosas adicionales.
     */
    @Query("""
            SELECT pa FROM PreguntaActividad pa
            JOIN FETCH pa.actividad a
            JOIN FETCH pa.pregunta p
            JOIN FETCH p.recurso r
            JOIN FETCH r.contenido c
            WHERE a.id IN :actividadIds
            """)
    List<PreguntaActividad> findAllConPreguntaByActividadIdIn(
            @Param("actividadIds") Collection<Long> actividadIds);
}
