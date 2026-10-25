package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.evaluacion.intento.model.Intento;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reglas de acceso a intentos y respuestas. La regla de la seccion 8 es que un
 * estudiante solo ve y modifica sus propios intentos; UTP y DOCENTE pueden
 * consultarlos, pero no iniciarlos ni modificarlos.
 */
@Component
public class EvaluacionAccessPolicy {

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
                    "Un estudiante solo puede consultar sus propios intentos"
            );
        }
        return usuario.getId();
    }

    /**
     * Un intento solo lo inicia el estudiante dueno. La ruta ya esta limitada a
     * ESTUDIANTE; esta validacion evita que se inicie a nombre de otro.
     */
    public void assertPuedeIniciar(Long estudianteId, Usuario usuario) {
        requireAuthenticated(usuario);
        if (usuario.getRol() != Rol.ESTUDIANTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo un estudiante puede iniciar intentos"
            );
        }
        if (!usuario.getId().equals(estudianteId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante solo puede iniciar sus propios intentos"
            );
        }
    }

    public void assertPuedeLeer(Intento intento, Usuario usuario) {
        requireAuthenticated(usuario);
        if (usuario.getRol() == Rol.ESTUDIANTE
                && !intento.getEstudiante().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante solo puede consultar sus propios intentos"
            );
        }
    }

    public void assertPuedeOperar(Intento intento, Usuario usuario) {
        requireAuthenticated(usuario);
        if (usuario.getRol() != Rol.ESTUDIANTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el estudiante dueno del intento puede modificarlo"
            );
        }
        if (!intento.getEstudiante().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante solo puede modificar sus propios intentos"
            );
        }
    }

    private void requireAuthenticated(Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
    }
}
