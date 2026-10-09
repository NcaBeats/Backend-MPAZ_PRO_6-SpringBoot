package com.example.mpaz_pro_6springboot.evaluacion.intento.repository;

import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntentoRepository extends JpaRepository<Intento, Long> {

    List<Intento> findByEstudianteId(Long estudianteId);

    List<Intento> findByActividadId(Long actividadId);

    List<Intento> findByEstudianteIdAndActividadId(Long estudianteId, Long actividadId);

    List<Intento> findByEstudianteIdAndActividadIdOrderByNumeroAsc(Long estudianteId, Long actividadId);

    Optional<Intento> findByEstudianteIdAndActividadIdAndNumero(Long estudianteId, Long actividadId, Integer numero);

    @Query("SELECT i FROM Intento i WHERE i.estudiante.id = :estudianteId AND i.actividad.id = :actividadId AND i.fechaFin IS NOT NULL ORDER BY i.numero DESC")
    Optional<Intento> findUltimoCompletadoByEstudianteAndActividad(@Param("estudianteId") Long estudianteId, @Param("actividadId") Long actividadId);

    long countByEstudianteIdAndActividadId(Long estudianteId, Long actividadId);

    boolean existsByEstudianteIdAndActividadIdAndNumero(Long estudianteId, Long actividadId, Integer numero);

    boolean existsByActividadId(Long actividadId);
}
