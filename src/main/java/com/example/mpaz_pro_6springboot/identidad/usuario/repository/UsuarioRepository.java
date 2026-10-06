package com.example.mpaz_pro_6springboot.identidad.usuario.repository;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByCursoId(Long cursoId);

    List<Usuario> findByRolAndCursoId(Rol rol, Long cursoId);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.curso IS NULL")
    List<Usuario> findDocentesSinCurso(@Param("rol") Rol rol);
}
