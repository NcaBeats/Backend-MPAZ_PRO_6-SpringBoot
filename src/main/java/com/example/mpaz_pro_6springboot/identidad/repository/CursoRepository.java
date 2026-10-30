package com.example.mpaz_pro_6springboot.identidad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.identidad.model.Curso;

/**
 * Acceso a datos de {@link Curso}.
 */
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findAllByAnnio(Integer annio);

    List<Curso> findAllByNivelId(Long nivelId);

    List<Curso> findAllBySeccionId(Long seccionId);

    Optional<Curso> findByAnnioAndNivelIdAndSeccionId(Integer annio, Long nivelId, Long seccionId);

    boolean existsByAnnioAndNivelIdAndSeccionId(Integer annio, Long nivelId, Long seccionId);
}
