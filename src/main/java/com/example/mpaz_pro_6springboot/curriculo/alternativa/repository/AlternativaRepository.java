package com.example.mpaz_pro_6springboot.curriculo.alternativa.repository;

import com.example.mpaz_pro_6springboot.curriculo.alternativa.model.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {

    List<Alternativa> findByPreguntaId(Long preguntaId);

    Optional<Alternativa> findByPreguntaIdAndEsCorrectaTrue(Long preguntaId);
}
