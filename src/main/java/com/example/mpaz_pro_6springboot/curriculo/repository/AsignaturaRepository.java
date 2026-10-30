package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.Asignatura;

/**
 * Acceso a datos de {@link Asignatura}.
 */
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

    Optional<Asignatura> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Asignatura> findAllByOrderByNombreAsc();
}
