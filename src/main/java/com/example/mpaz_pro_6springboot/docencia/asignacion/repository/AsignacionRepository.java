package com.example.mpaz_pro_6springboot.docencia.asignacion.repository;

import com.example.mpaz_pro_6springboot.docencia.asignacion.model.Asignacion;
import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findAllByOrderByIdAsc();

    List<Asignacion> findByCursoId(Long cursoId);

    List<Asignacion> findByCursoIdOrderByIdAsc(Long cursoId);

    List<Asignacion> findByUnidadId(Long unidadId);

    List<Asignacion> findByUnidadIdOrderByIdAsc(Long unidadId);

    List<Asignacion> findByDocenteId(Long docenteId);

    List<Asignacion> findByDocenteIdOrderByIdAsc(Long docenteId);

    @Query("SELECT DISTINCT a.curso FROM Asignacion a WHERE a.docente.id = :docenteId ORDER BY a.curso.id")
    List<Curso> findCursosByDocenteId(@Param("docenteId") Long docenteId);

    List<Asignacion> findByCursoIdAndUnidadIdOrderByIdAsc(Long cursoId, Long unidadId);

    List<Asignacion> findByCursoIdAndDocenteIdOrderByIdAsc(Long cursoId, Long docenteId);

    List<Asignacion> findByUnidadIdAndDocenteIdOrderByIdAsc(Long unidadId, Long docenteId);

    List<Asignacion> findByCursoIdAndUnidadIdAndDocenteIdOrderByIdAsc(Long cursoId, Long unidadId, Long docenteId);

    Optional<Asignacion> findByCursoIdAndUnidadId(Long cursoId, Long unidadId);

    @Query("SELECT a FROM Asignacion a WHERE a.curso.id = :cursoId AND a.unidad.id = :unidadId AND a.maxIntentos IS NOT NULL")
    Optional<Asignacion> findWithMaxIntentosByCursoAndUnidad(@Param("cursoId") Long cursoId, @Param("unidadId") Long unidadId);

    boolean existsByCursoIdAndUnidadId(Long cursoId, Long unidadId);

    boolean existsByCursoId(Long cursoId);

    boolean existsByUnidadId(Long unidadId);

    boolean existsByDocenteId(Long docenteId);
}
