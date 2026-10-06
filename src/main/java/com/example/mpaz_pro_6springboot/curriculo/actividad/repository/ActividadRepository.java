package com.example.mpaz_pro_6springboot.curriculo.actividad.repository;

import com.example.mpaz_pro_6springboot.curriculo.actividad.model.Actividad;
import com.example.mpaz_pro_6springboot.common.enums.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    List<Actividad> findByUnidadId(Long unidadId);

    List<Actividad> findByUnidadIdOrderByOrdenAsc(Long unidadId);

    List<Actividad> findByUnidadIdAndTipo(Long unidadId, TipoActividad tipo);

    Optional<Actividad> findByUnidadIdAndTipoAndTitulo(Long unidadId, TipoActividad tipo, String titulo);

    @Query("SELECT a FROM Actividad a WHERE a.unidad.id = :unidadId AND a.tipo = 'PRUEBA_FINAL'")
    Optional<Actividad> findPruebaFinalByUnidadId(@Param("unidadId") Long unidadId);

    boolean existsByUnidadIdAndTitulo(Long unidadId, String titulo);
}
