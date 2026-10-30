package com.example.mpaz_pro_6springboot.curriculo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.curriculo.model.ObjetivoAprendizaje;

/**
 * Acceso a datos de {@link ObjetivoAprendizaje}.
 *
 * <p>{@code codigo} no es unico en el esquema, por eso las busquedas por
 * codigo devuelven listas.</p>
 */
public interface ObjetivoAprendizajeRepository extends JpaRepository<ObjetivoAprendizaje, Long> {

    List<ObjetivoAprendizaje> findAllByCodigo(String codigo);

    List<ObjetivoAprendizaje> findAllByOrderByCodigoAsc();

    boolean existsByCodigo(String codigo);
}
