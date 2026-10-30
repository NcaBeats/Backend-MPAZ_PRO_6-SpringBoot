package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.Recurso;

/**
 * Acceso a datos de {@link Recurso}.
 */
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    List<Recurso> findAllByContenidoId(Long contenidoId);

    List<Recurso> findAllByTipoRecursoId(Long tipoRecursoId);

    List<Recurso> findAllByContenidoIdAndTipoRecursoId(Long contenidoId, Long tipoRecursoId);
}
