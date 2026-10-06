package com.example.mpaz_pro_6springboot.informe.detalle_informe_oa;

import com.example.mpaz_pro_6springboot.informe.detalle_informe_oa.model.DetalleInformeOa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleInformeOaRepository extends JpaRepository<DetalleInformeOa, Long> {

    List<DetalleInformeOa> findByInformeId(Long informeId);

    Optional<DetalleInformeOa> findByInformeIdAndOaId(Long informeId, Long oaId);

    boolean existsByInformeIdAndOaId(Long informeId, Long oaId);
}
