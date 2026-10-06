package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.repository;

import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoContenidoRepository extends JpaRepository<RecursoContenido, Long> {

    List<RecursoContenido> findByContenidoId(Long contenidoId);
}
