package com.example.mpaz_pro_6springboot.evaluacion.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mpaz_pro_6springboot.evaluacion.model.Respuesta;

/**
 * Acceso a datos de {@link Respuesta}.
 */
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    List<Respuesta> findAllByIntentoId(Long intentoId);

    List<Respuesta> findAllByPreguntaId(Long preguntaId);

    List<Respuesta> findAllByIntentoIdAndPreguntaId(Long intentoId, Long preguntaId);

    boolean existsByIntentoIdAndPreguntaIdAndAlternativaId(
            Long intentoId, Long preguntaId, Long alternativaId);

    /**
     * Carga las respuestas de un conjunto de intentos con la pregunta y la
     * alternativa ya resueltas, para evaluar correctas/incorrectas sin
     * consultas perezosas adicionales.
     */
    @Query("""
            SELECT r FROM Respuesta r
            JOIN FETCH r.intento i
            JOIN FETCH r.pregunta p
            JOIN FETCH r.alternativa a
            WHERE i.id IN :intentoIds
            """)
    List<Respuesta> findAllConDetalleByIntentoIdIn(
            @Param("intentoIds") Collection<Long> intentoIds);
}
