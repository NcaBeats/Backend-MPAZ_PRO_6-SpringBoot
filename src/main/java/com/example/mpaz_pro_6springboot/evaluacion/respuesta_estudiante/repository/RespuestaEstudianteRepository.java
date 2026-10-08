package com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.repository;

import com.example.mpaz_pro_6springboot.evaluacion.respuesta_estudiante.model.RespuestaEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaEstudianteRepository extends JpaRepository<RespuestaEstudiante, Long> {

    List<RespuestaEstudiante> findByIntentoId(Long intentoId);

    List<RespuestaEstudiante> findByPreguntaId(Long preguntaId);

    Optional<RespuestaEstudiante> findByIntentoIdAndPreguntaId(Long intentoId, Long preguntaId);

    boolean existsByIntentoIdAndPreguntaId(Long intentoId, Long preguntaId);

    boolean existsByAlternativaId(Long alternativaId);
}
