package com.example.mpaz_pro_6springboot.evaluacion.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.mpaz_pro_6springboot.evaluacion.model.Intento;

/**
 * Acceso a datos de {@link Intento}.
 */
public interface IntentoRepository extends JpaRepository<Intento, Long> {

    List<Intento> findAllByUsuarioId(Long usuarioId);

    List<Intento> findAllByActividadCursoId(Long actividadCursoId);

    List<Intento> findAllByUsuarioIdAndActividadCursoId(Long usuarioId, Long actividadCursoId);

    List<Intento> findAllByActividadCursoActividadId(Long actividadId);

    List<Intento> findAllByUsuarioIdAndActividadCursoActividadId(Long usuarioId, Long actividadId);

    long countByUsuarioIdAndActividadCursoId(Long usuarioId, Long actividadCursoId);

    /**
     * Obtiene el id del ultimo intento cerrado de cada actividad de la unidad,
     * considerando solo intentos realizados hasta {@code fechaCorte} y en
     * cursos en los que el usuario esta inscrito.
     *
     * <p>Se agrupa por actividad (no por {@code actividad_curso}) para no
     * duplicar el resultado si la misma actividad se publico en varios cursos.
     * El desempate usa el id del intento.</p>
     */
    @Query(value = """
            SELECT DISTINCT ON (a.actividad_id) i.intento_id
            FROM intento i
            JOIN actividad_curso ac ON ac.actividad_curso_id = i.actividad_curso_id
            JOIN actividad a ON a.actividad_id = ac.actividad_id
            JOIN contenido c ON c.contenido_id = a.contenido_id
            JOIN usuario_curso uc ON uc.curso_id = ac.curso_id AND uc.usuario_id = i.usuario_id
            WHERE i.usuario_id = :usuarioId
              AND c.unidad_id = :unidadId
              AND i.fecha_fin IS NOT NULL
              AND i.fecha_fin <= :fechaCorte
            ORDER BY a.actividad_id, i.fecha_fin DESC, i.intento_id DESC
            """, nativeQuery = true)
    List<Long> findIdsUltimosIntentosPorUnidad(
            @Param("usuarioId") Long usuarioId,
            @Param("unidadId") Long unidadId,
            @Param("fechaCorte") Instant fechaCorte);

    /**
     * Carga los intentos con su actividad y contenido para evitar consultas
     * perezosas durante el calculo del informe.
     */
    @Query("""
            SELECT i FROM Intento i
            JOIN FETCH i.actividadCurso ac
            JOIN FETCH ac.actividad a
            JOIN FETCH a.contenido c
            WHERE i.id IN :ids
            """)
    List<Intento> findAllConActividadByIdIn(@Param("ids") Collection<Long> ids);
}
