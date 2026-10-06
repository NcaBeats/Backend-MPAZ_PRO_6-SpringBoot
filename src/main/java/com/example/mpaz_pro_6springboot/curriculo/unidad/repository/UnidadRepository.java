package com.example.mpaz_pro_6springboot.curriculo.unidad;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {

    List<Unidad> findByAsignaturaId(Long asignaturaId);

    List<Unidad> findByAsignaturaIdOrderByOrdenAsc(Long asignaturaId);

    List<Unidad> findByEstado(EstadoContenido estado);

    List<Unidad> findByAsignaturaIdAndEstado(Long asignaturaId, EstadoContenido estado);

    Optional<Unidad> findByAsignaturaIdAndTitulo(Long asignaturaId, String titulo);

    @Query("SELECT u FROM Unidad u WHERE u.estado IN ('AUTORIZADO', 'PUBLICADO') AND u.asignatura.id = :asignaturaId ORDER BY u.orden")
    List<Unidad> findAutorizadasByAsignaturaId(@Param("asignaturaId") Long asignaturaId);

    boolean existsByAsignaturaIdAndTitulo(Long asignaturaId, String titulo);
}
