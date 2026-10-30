package com.example.mpaz_pro_6springboot.identidad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mpaz_pro_6springboot.identidad.model.Usuario;

/**
 * Acceso a datos de {@link Usuario}.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    List<Usuario> findAllByRolId(Long rolId);

    List<Usuario> findAllByActivoTrue();
}
