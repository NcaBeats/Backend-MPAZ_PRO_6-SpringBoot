package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Carga las relaciones OA-contenido de una unidad con el OA y el contenido
     * ya resueltos, para agrupar los objetivos por contenido.
     */
    @Query("""
            SELECT oac FROM ObjetivoAprendizajeContenido oac
            JOIN FETCH oac.objetivoAprendizaje oa
            JOIN FETCH oac.contenido c
            WHERE oac.unidad.id = :unidadId
            """)
    List<ObjetivoAprendizajeContenido> findAllConObjetivoByUnidadId(
            @Param("unidadId") Long unidadId);
}
