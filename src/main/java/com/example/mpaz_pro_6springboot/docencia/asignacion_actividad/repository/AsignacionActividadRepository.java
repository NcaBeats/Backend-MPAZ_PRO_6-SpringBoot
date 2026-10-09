package com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.repository;

import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividad;
import com.example.mpaz_pro_6springboot.docencia.asignacion_actividad.model.AsignacionActividadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionActividadRepository extends JpaRepository<AsignacionActividad, AsignacionActividadId> {

    List<AsignacionActividad> findByAsignacionId(Long asignacionId);

    List<AsignacionActividad> findByActividadId(Long actividadId);

    Optional<AsignacionActividad> findByAsignacionIdAndActividadId(Long asignacionId, Long actividadId);

    boolean existsByAsignacionIdAndActividadId(Long asignacionId, Long actividadId);

    boolean existsByActividadId(Long actividadId);

    boolean existsByAsignacionId(Long asignacionId);
}
