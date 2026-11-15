package com.example.mpaz_pro_6springboot.identidad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.identidad.model.Nivel;

/**
 * Acceso a datos del catalogo {@link Nivel}.
 */
public interface NivelRepository extends JpaRepository<Nivel, Long> {

    Optional<Nivel> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Nivel> findAllByOrderByNombreAsc();
}
