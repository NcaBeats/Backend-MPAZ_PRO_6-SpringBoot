package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.TipoActividad;

/**
 * Acceso a datos del catalogo {@link TipoActividad}.
 */
public interface TipoActividadRepository extends JpaRepository<TipoActividad, Long> {

    Optional<TipoActividad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<TipoActividad> findAllByOrderByNombreAsc();
}
