package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.Pregunta;

/**
 * Acceso a datos de {@link Pregunta}.
 */
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findAllByRecursoId(Long recursoId);

    List<Pregunta> findAllByRecursoContenidoId(Long contenidoId);
}
