package com.example.mpaz_pro_6springboot.identidad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.identidad.model.Seccion;

/**
 * Acceso a datos del catalogo {@link Seccion}.
 */
public interface SeccionRepository extends JpaRepository<Seccion, Long> {

    Optional<Seccion> findByLetra(String letra);

    boolean existsByLetra(String letra);

    List<Seccion> findAllByOrderByLetraAsc();
}
