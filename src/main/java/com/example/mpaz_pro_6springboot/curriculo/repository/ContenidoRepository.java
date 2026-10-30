package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.Contenido;

/**
 * Acceso a datos de {@link Contenido}.
 *
 * <p>La unidad del contenido es inmutable; aqui solo se consulta.</p>
 */
public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    List<Contenido> findAllByUnidadId(Long unidadId);

    List<Contenido> findAllByEstadoId(Long estadoId);

    List<Contenido> findAllByUnidadIdAndEstadoId(Long unidadId, Long estadoId);
}
