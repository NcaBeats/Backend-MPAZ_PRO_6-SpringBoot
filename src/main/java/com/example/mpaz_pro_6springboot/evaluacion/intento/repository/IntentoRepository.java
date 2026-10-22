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

    List<Intento> findByEstudianteIdOrderByActividadIdAscNumeroAsc(Long estudianteId);

    List<Intento> findByActividadId(Long actividadId);

    List<Intento> findByActividadIdOrderByEstudianteIdAscNumeroAsc(Long actividadId);

    List<Intento> findAllByOrderByEstudianteIdAscActividadIdAscNumeroAsc();

    List<Intento> findByEstudianteIdAndActividadId(Long estudianteId, Long actividadId);

    List<Intento> findByEstudianteIdAndActividadIdOrderByNumeroAsc(Long estudianteId, Long actividadId);

    Optional<Intento> findByEstudianteIdAndActividadIdAndNumero(Long estudianteId, Long actividadId, Integer numero);

    @Query("SELECT COALESCE(MAX(i.numero), 0) FROM Intento i WHERE i.estudiante.id = :estudianteId AND i.actividad.id = :actividadId")
    Integer findMaxNumero(@Param("estudianteId") Long estudianteId, @Param("actividadId") Long actividadId);

    // El ultimo intento completado: la consulta derivada aplica LIMIT 1. Con
    // @Query y Optional, varios intentos completados provocaban
    // NonUniqueResultException.
    Optional<Intento> findFirstByEstudianteIdAndActividadIdAndFechaFinIsNotNullOrderByNumeroDesc(
            Long estudianteId, Long actividadId);

    long countByEstudianteIdAndActividadId(Long estudianteId, Long actividadId);

    boolean existsByEstudianteIdAndActividadIdAndNumero(Long estudianteId, Long actividadId, Integer numero);

    boolean existsByActividadId(Long actividadId);

    boolean existsByEstudianteId(Long estudianteId);

    boolean existsByActividadUnidadIdAndEstudianteCursoId(Long unidadId, Long cursoId);
}
