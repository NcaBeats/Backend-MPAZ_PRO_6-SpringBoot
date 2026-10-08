package com.example.mpaz_pro_6springboot.curriculo.alternativa.repository;

import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {

    List<Alternativa> findByPreguntaId(Long preguntaId);

    List<Alternativa> findByPreguntaIdOrderByIdAsc(Long preguntaId);

    List<Alternativa> findAllByOrderByPreguntaIdAscIdAsc();

    Optional<Alternativa> findByPreguntaIdAndEsCorrectaTrue(Long preguntaId);

    boolean existsByPreguntaIdAndEsCorrectaTrue(Long preguntaId);

    boolean existsByPreguntaIdAndEsCorrectaTrueAndIdNot(Long preguntaId, Long id);
}
