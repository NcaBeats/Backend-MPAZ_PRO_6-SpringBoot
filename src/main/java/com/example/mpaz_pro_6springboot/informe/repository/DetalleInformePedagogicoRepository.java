package com.example.mpaz_pro_6springboot.informe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.informe.model.DetalleInformePedagogico;

/**
 * Acceso a datos de {@link DetalleInformePedagogico}.
 */
public interface DetalleInformePedagogicoRepository
        extends JpaRepository<DetalleInformePedagogico, Long> {

    List<DetalleInformePedagogico> findAllByInformePedagogicoId(Long informePedagogicoId);

    List<DetalleInformePedagogico> findAllByObjetivoAprendizajeId(Long objetivoAprendizajeId);

    boolean existsByInformePedagogicoIdAndObjetivoAprendizajeId(
            Long informePedagogicoId, Long objetivoAprendizajeId);
}
