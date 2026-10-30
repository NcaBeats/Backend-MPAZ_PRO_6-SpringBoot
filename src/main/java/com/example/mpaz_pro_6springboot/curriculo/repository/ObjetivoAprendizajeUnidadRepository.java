package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeUnidad;

/**
 * Acceso a datos de la relacion N:M {@link ObjetivoAprendizajeUnidad}.
 */
public interface ObjetivoAprendizajeUnidadRepository
        extends JpaRepository<ObjetivoAprendizajeUnidad, Long> {

    List<ObjetivoAprendizajeUnidad> findAllByUnidadId(Long unidadId);

    List<ObjetivoAprendizajeUnidad> findAllByObjetivoAprendizajeId(Long objetivoAprendizajeId);

    boolean existsByObjetivoAprendizajeIdAndUnidadId(Long objetivoAprendizajeId, Long unidadId);

    long countByUnidadId(Long unidadId);
}
