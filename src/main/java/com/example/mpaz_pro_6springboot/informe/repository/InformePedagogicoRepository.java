package com.example.mpaz_pro_6springboot.informe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.informe.model.InformePedagogico;

/**
 * Acceso a datos de {@link InformePedagogico}.
 */
public interface InformePedagogicoRepository extends JpaRepository<InformePedagogico, Long> {

    List<InformePedagogico> findAllByUsuarioId(Long usuarioId);

    List<InformePedagogico> findAllByUnidadId(Long unidadId);

    List<InformePedagogico> findAllByUsuarioIdAndUnidadId(Long usuarioId, Long unidadId);
}
