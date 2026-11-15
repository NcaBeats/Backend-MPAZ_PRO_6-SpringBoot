package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.EstadoAutorizacion;

/**
 * Acceso a datos del catalogo {@link EstadoAutorizacion}.
 */
public interface EstadoAutorizacionRepository extends JpaRepository<EstadoAutorizacion, Long> {

    Optional<EstadoAutorizacion> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<EstadoAutorizacion> findAllByOrderByNombreAsc();
}
