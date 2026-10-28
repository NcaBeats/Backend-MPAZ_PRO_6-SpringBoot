package com.example.mpaz_pro_6springboot.docencia.docencia.repository;

import com.example.mpaz_pro_6springboot.docencia.docencia.model.DocenteCursoAsignatura;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteCursoAsignaturaRepository
        extends JpaRepository<DocenteCursoAsignatura, Long> {

    @Override
    @EntityGraph(attributePaths = {"docente", "curso", "asignatura"})
    Optional<DocenteCursoAsignatura> findById(Long id);

    @EntityGraph(attributePaths = {"docente", "curso", "asignatura"})
    List<DocenteCursoAsignatura> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"docente", "curso", "asignatura"})
    List<DocenteCursoAsignatura> findByDocenteIdOrderByIdAsc(Long docenteId);

    @EntityGraph(attributePaths = {"docente", "curso", "asignatura"})
    Optional<DocenteCursoAsignatura> findByCursoIdAndAsignaturaId(
            Long cursoId,
            Long asignaturaId
    );

    boolean existsByCursoIdAndAsignaturaId(Long cursoId, Long asignaturaId);

    boolean existsByCursoIdAndAsignaturaIdAndIdNot(
            Long cursoId,
            Long asignaturaId,
            Long id
    );

    boolean existsByDocenteId(Long docenteId);

    boolean existsByDocenteIdAndCursoId(Long docenteId, Long cursoId);

    boolean existsByDocenteIdAndAsignaturaId(Long docenteId, Long asignaturaId);
}
