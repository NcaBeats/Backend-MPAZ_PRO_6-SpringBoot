package com.example.mpaz_pro_6springboot.identidad.curso.repository;

import com.example.mpaz_pro_6springboot.identidad.curso.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByNombreAndAnio(String nombre, Integer anio);

    List<Curso> findByAnio(Integer anio);

    @Query("SELECT c FROM Curso c WHERE c.anio = :anio ORDER BY c.nombre")
    List<Curso> findByAnioOrderByNombre(@Param("anio") Integer anio);

    List<Curso> findAllByOrderByAnioAscNombreAsc();

    boolean existsByNombreAndAnio(String nombre, Integer anio);

    boolean existsByNombreAndAnioAndIdNot(String nombre, Integer anio, Long id);

    List<Curso> findAllByAnio(Integer anio);
}
