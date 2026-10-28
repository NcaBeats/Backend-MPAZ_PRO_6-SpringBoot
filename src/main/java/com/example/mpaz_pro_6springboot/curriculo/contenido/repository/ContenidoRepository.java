package com.example.mpaz_pro_6springboot.curriculo.contenido.repository;

import com.example.mpaz_pro_6springboot.curriculo.contenido.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    List<Contenido> findByOaId(Long oaId);

    List<Contenido> findByOaIdOrderByOrdenAscIdAsc(Long oaId);

    List<Contenido> findAllByOrderByOaIdAscOrdenAscIdAsc();

    boolean existsByOaId(Long oaId);

    @Query("SELECT c FROM Contenido c WHERE c.oa.unidad.id = :unidadId ORDER BY c.id")
    List<Contenido> findByUnidadId(@Param("unidadId") Long unidadId);
}
