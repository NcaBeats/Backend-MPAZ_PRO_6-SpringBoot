package com.example.mpaz_pro_6springboot.identidad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.identidad.model.Rol;

/**
 * Acceso a datos del catalogo {@link Rol}.
 */
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Rol> findAllByOrderByNombreAsc();
}
