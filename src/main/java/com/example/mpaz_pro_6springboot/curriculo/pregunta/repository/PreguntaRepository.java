package com.example.mpaz_pro_6springboot.curriculo.pregunta.repository;

import com.example.mpaz_pro_6springboot.curriculo.pregunta.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByActividadId(Long actividadId);

    List<Pregunta> findByActividadIdOrderByOrdenAsc(Long actividadId);

    List<Pregunta> findAllByOrderByActividadIdAscOrdenAscIdAsc();

    List<Pregunta> findByOaId(Long oaId);

    List<Pregunta> findByOaIdOrderByOrdenAscIdAsc(Long oaId);

    List<Pregunta> findByActividadIdAndOaIdOrderByOrdenAscIdAsc(Long actividadId, Long oaId);

    List<Pregunta> findByUnidadId(Long unidadId);

    Optional<Pregunta> findByActividadIdAndOrden(Long actividadId, Integer orden);

    boolean existsByOaId(Long oaId);

    boolean existsByActividadId(Long actividadId);
}
