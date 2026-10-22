package com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.repository;

import com.example.mpaz_pro_6springboot.curriculo.objetivo_aprendizaje.model.ObjetivoAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObjetivoAprendizajeRepository extends JpaRepository<ObjetivoAprendizaje, Long> {

    List<ObjetivoAprendizaje> findByUnidadId(Long unidadId);

    List<ObjetivoAprendizaje> findByUnidadIdOrderByCodigoAsc(Long unidadId);

    List<ObjetivoAprendizaje> findAllByOrderByUnidadIdAscCodigoAsc();

    Optional<ObjetivoAprendizaje> findByUnidadIdAndCodigo(Long unidadId, String codigo);

    @Query("SELECT oa FROM ObjetivoAprendizaje oa WHERE oa.unidad.id = :unidadId AND oa.codigo IS NOT NULL ORDER BY oa.codigo")
    List<ObjetivoAprendizaje> findByUnidadIdWithCodigo(@Param("unidadId") Long unidadId);

    boolean existsByUnidadIdAndCodigo(Long unidadId, String codigo);

    boolean existsByUnidadIdAndCodigoAndIdNot(Long unidadId, String codigo, Long id);

    boolean existsByUnidadId(Long unidadId);
}
