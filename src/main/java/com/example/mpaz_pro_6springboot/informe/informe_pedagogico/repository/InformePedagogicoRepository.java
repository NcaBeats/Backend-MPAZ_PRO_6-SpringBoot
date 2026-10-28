package com.example.mpaz_pro_6springboot.informe.informe_pedagogico.repository;

import com.example.mpaz_pro_6springboot.common.enums.EstadoInforme;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InformePedagogicoRepository extends JpaRepository<InformePedagogico, Long> {

    @EntityGraph(attributePaths = {"estudiante", "unidad", "unidad.asignatura", "docente"})
    List<InformePedagogico> findAllByOrderByIdAsc();

    List<InformePedagogico> findByEstudianteId(Long estudianteId);

    @EntityGraph(attributePaths = {"estudiante", "unidad", "unidad.asignatura", "docente"})
    List<InformePedagogico> findByEstudianteIdOrderByIdAsc(Long estudianteId);

    List<InformePedagogico> findByUnidadId(Long unidadId);

    @EntityGraph(attributePaths = {"estudiante", "unidad", "unidad.asignatura", "docente"})
    List<InformePedagogico> findByUnidadIdOrderByIdAsc(Long unidadId);

    @EntityGraph(attributePaths = {"estudiante", "unidad", "unidad.asignatura", "docente"})
    List<InformePedagogico> findByEstudianteIdAndUnidadIdOrderByIdAsc(Long estudianteId, Long unidadId);

    List<InformePedagogico> findByDocenteId(Long docenteId);

    List<InformePedagogico> findByEstado(EstadoInforme estado);

    Optional<InformePedagogico> findByEstudianteIdAndUnidadId(Long estudianteId, Long unidadId);

    @Override
    @EntityGraph(attributePaths = {"estudiante", "unidad", "unidad.asignatura", "docente"})
    Optional<InformePedagogico> findById(Long id);

    @Query("SELECT i FROM InformePedagogico i WHERE i.estudiante.id = :estudianteId AND i.unidad.id = :unidadId AND i.estado = :estado")
    Optional<InformePedagogico> findByEstudianteAndUnidadAndEstado(@Param("estudianteId") Long estudianteId, @Param("unidadId") Long unidadId, @Param("estado") EstadoInforme estado);

    boolean existsByEstudianteIdAndUnidadId(Long estudianteId, Long unidadId);

    boolean existsByEstudianteId(Long estudianteId);

    boolean existsByDocenteId(Long docenteId);

    boolean existsByUnidadId(Long unidadId);
}
