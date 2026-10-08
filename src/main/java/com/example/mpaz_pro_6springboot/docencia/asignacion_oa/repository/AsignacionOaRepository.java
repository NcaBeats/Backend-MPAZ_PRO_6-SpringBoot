package com.example.mpaz_pro_6springboot.docencia.asignacion_oa.repository;

import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOa;
import com.example.mpaz_pro_6springboot.docencia.asignacion_oa.model.AsignacionOaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionOaRepository extends JpaRepository<AsignacionOa, AsignacionOaId> {

    List<AsignacionOa> findByAsignacionId(Long asignacionId);

    List<AsignacionOa> findByObjetivoAprendizajeId(Long oaId);

    Optional<AsignacionOa> findByAsignacionIdAndObjetivoAprendizajeId(Long asignacionId, Long oaId);

    boolean existsByAsignacionIdAndObjetivoAprendizajeId(Long asignacionId, Long oaId);

    boolean existsByObjetivoAprendizajeId(Long oaId);
}
