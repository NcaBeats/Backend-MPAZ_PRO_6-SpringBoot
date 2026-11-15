package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.TipoRecurso;

/**
 * Acceso a datos del catalogo {@link TipoRecurso}.
 */
public interface TipoRecursoRepository extends JpaRepository<TipoRecurso, Long> {

    Optional<TipoRecurso> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<TipoRecurso> findAllByOrderByNombreAsc();
}
