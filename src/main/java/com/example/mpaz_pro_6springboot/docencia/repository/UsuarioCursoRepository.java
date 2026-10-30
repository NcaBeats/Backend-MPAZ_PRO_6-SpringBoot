package com.example.mpaz_pro_6springboot.docencia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.docencia.model.UsuarioCurso;

/**
 * Acceso a datos de la relacion {@link UsuarioCurso}.
 */
public interface UsuarioCursoRepository extends JpaRepository<UsuarioCurso, Long> {

    List<UsuarioCurso> findAllByUsuarioId(Long usuarioId);

    List<UsuarioCurso> findAllByCursoId(Long cursoId);

    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
}
