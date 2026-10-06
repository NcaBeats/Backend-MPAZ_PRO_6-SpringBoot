package com.example.mpaz_pro_6springboot.curriculo.asignatura.repository;

import com.example.mpaz_pro_6springboot.curriculo.asignatura.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

    Optional<Asignatura> findByNombre(String nombre);

    List<Asignatura> findAllByOrderByNombreAsc();

    boolean existsByNombre(String nombre);
}
