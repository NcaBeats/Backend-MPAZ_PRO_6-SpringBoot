package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.Alternativa;

/**
 * Acceso a datos de {@link Alternativa}.
 */
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {

    List<Alternativa> findAllByRecursoId(Long recursoId);

    List<Alternativa> findAllByRecursoIdAndCorrectaTrue(Long recursoId);
}
