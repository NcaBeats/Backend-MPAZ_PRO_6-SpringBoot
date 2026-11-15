package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.Unidad;

/**
 * Acceso a datos de {@link Unidad}.
 */
public interface UnidadRepository extends JpaRepository<Unidad, Long> {

    List<Unidad> findAllByAsignaturaId(Long asignaturaId);

    List<Unidad> findAllByEstadoId(Long estadoId);

    List<Unidad> findAllByAsignaturaIdAndEstadoId(Long asignaturaId, Long estadoId);

    boolean existsByAsignaturaIdAndNombre(Long asignaturaId, String nombre);
}
