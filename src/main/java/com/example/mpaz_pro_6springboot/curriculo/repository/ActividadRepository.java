package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.Actividad;

/**
 * Acceso a datos de {@link Actividad}.
 *
 * <p>La unidad de la actividad se consulta a traves del contenido asociado.</p>
 */
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    List<Actividad> findAllByContenidoId(Long contenidoId);

    List<Actividad> findAllByContenidoUnidadId(Long unidadId);

    List<Actividad> findAllByContenidoIdAndHabilitadaTrue(Long contenidoId);

    List<Actividad> findAllByEstadoId(Long estadoId);
}
