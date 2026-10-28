package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reglas de acceso a los informes pedagogicos. La regla base de la seccion 8
 * es que un estudiante solo puede ver sus propios datos; UTP y DOCENTE pueden
 * consultar los informes, pero un DOCENTE solo genera/valida los suyos.
 */
@Component
public class InformeAccessPolicy {

    /**
     * Si el usuario es estudiante, fuerza el filtro a su propio id y rechaza
     * cualquier intento de consultar a otro estudiante.
     */
    public Long scopeEstudianteId(Long estudianteIdSolicitado, Usuario usuario) {
        requireAuthenticated(usuario);
        if (usuario.getRol() != Rol.ESTUDIANTE) {
            return estudianteIdSolicitado;
        }
        if (estudianteIdSolicitado != null && !estudianteIdSolicitado.equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante solo puede consultar sus propios informes"
            );
        }
        return usuario.getId();
    }

    public void assertCanRead(InformePedagogico informe, Usuario usuario) {
        requireAuthenticated(usuario);
        if (usuario.getRol() == Rol.ESTUDIANTE
                && !informe.getEstudiante().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante solo puede consultar sus propios informes"
            );
        }
    }

    public void assertCanGenerate(Long docenteId, Usuario usuario) {
        requireAuthenticated(usuario);
        if (usuario.getRol() == Rol.ESTUDIANTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El estudiante no puede generar informes"
            );
        }
        if (usuario.getRol() == Rol.DOCENTE && !docenteId.equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un docente solo puede generar informes como responsable"
            );
        }
    }

    public void assertCanValidate(InformePedagogico informe, Usuario usuario) {
        requireAuthenticated(usuario);
        if (usuario.getRol() == Rol.ESTUDIANTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El estudiante no puede validar informes"
            );
        }
        if (usuario.getRol() == Rol.DOCENTE
                && !informe.getDocente().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un docente solo puede validar los informes que genero"
            );
        }
    }

    private void requireAuthenticated(Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
    }
}
