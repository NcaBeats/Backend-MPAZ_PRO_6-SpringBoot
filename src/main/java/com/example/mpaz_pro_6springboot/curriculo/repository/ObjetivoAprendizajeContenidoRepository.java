package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizajeContenido;

/**
 * Acceso a datos de la relacion N:M {@link ObjetivoAprendizajeContenido}.
 */
public interface ObjetivoAprendizajeContenidoRepository
        extends JpaRepository<ObjetivoAprendizajeContenido, Long> {

    List<ObjetivoAprendizajeContenido> findAllByContenidoId(Long contenidoId);

    List<ObjetivoAprendizajeContenido> findAllByObjetivoAprendizajeId(Long objetivoAprendizajeId);

    List<ObjetivoAprendizajeContenido> findAllByUnidadId(Long unidadId);

    boolean existsByContenidoIdAndObjetivoAprendizajeId(
            Long contenidoId, Long objetivoAprendizajeId);
}
