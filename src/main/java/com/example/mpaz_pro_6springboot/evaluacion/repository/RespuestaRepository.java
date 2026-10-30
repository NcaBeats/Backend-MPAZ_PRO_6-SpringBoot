package com.example.mpaz_pro_6springboot.evaluacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
