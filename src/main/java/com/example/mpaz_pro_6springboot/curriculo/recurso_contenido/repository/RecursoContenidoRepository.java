package com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.repository;

import com.example.mpaz_pro_6springboot.curriculo.recurso_contenido.model.RecursoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoContenidoRepository extends JpaRepository<RecursoContenido, Long> {

    List<RecursoContenido> findByContenidoId(Long contenidoId);

    @Query("SELECT r FROM RecursoContenido r WHERE r.contenido.oa.unidad.id = :unidadId ORDER BY r.id")
    List<RecursoContenido> findByUnidadId(@Param("unidadId") Long unidadId);
}
