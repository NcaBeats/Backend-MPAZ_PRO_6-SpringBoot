package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.AlternativaPregunta;

/**
 * Acceso a datos de la relacion N:M {@link AlternativaPregunta}.
 */
public interface AlternativaPreguntaRepository extends JpaRepository<AlternativaPregunta, Long> {

    List<AlternativaPregunta> findAllByPreguntaId(Long preguntaId);

    List<AlternativaPregunta> findAllByAlternativaId(Long alternativaId);

    boolean existsByPreguntaIdAndAlternativaId(Long preguntaId, Long alternativaId);

    long countByPreguntaIdAndAlternativaCorrectaTrue(Long preguntaId);
}
