package com.example.mpaz_pro_6springboot.docencia.asignacion.repository;

import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByCursoId(Long cursoId);

    List<Asignacion> findByUnidadId(Long unidadId);

    List<Asignacion> findByDocenteId(Long docenteId);

    Optional<Asignacion> findByCursoIdAndUnidadId(Long cursoId, Long unidadId);

    @Query("SELECT a FROM Asignacion a WHERE a.curso.id = :cursoId AND a.unidad.id = :unidadId AND a.maxIntentos IS NOT NULL")
    Optional<Asignacion> findWithMaxIntentosByCursoAndUnidad(@Param("cursoId") Long cursoId, @Param("unidadId") Long unidadId);

    boolean existsByCursoIdAndUnidadId(Long cursoId, Long unidadId);

    boolean existsByCursoId(Long cursoId);

    boolean existsByUnidadId(Long unidadId);
}
