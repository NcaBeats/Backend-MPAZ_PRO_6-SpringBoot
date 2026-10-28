package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import com.example.mpaz_pro_6springboot.informe.informe_pedagogico.model.InformePedagogico;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reglas de acceso a los informes pedagogicos.
 *
 * <p>Los informes son exclusivos del rol DOCENTE. Un docente solo puede
 * consultar, generar y validar los informes en los que figura como responsable.
 * Los estudiantes y la UTP no tienen acceso.</p>
 */
@Component
public class InformeAccessPolicy {

    /** Valida que el usuario autenticado pueda listar informes. */
    public void assertCanList(Usuario usuario) {
        requireDocente(usuario);
    }

    /** Valida que el docente sea el responsable del informe. */
    public void assertCanRead(InformePedagogico informe, Usuario usuario) {
        requireDocente(usuario);
        if (!informe.getDocente().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un docente solo puede consultar sus propios informes"
            );
        }
    }

    /** Valida que el docente genere el informe como responsable. */
    public void assertCanGenerate(Long docenteId, Usuario usuario) {
        requireDocente(usuario);
        if (docenteId == null || !docenteId.equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un docente solo puede generar informes como responsable"
            );
        }
    }

    /** Valida que el docente sea el responsable del informe que valida. */
    public void assertCanValidate(InformePedagogico informe, Usuario usuario) {
        requireDocente(usuario);
        if (!informe.getDocente().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un docente solo puede validar sus propios informes"
            );
        }
    }

    private void requireDocente(Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        if (usuario.getRol() != Rol.DOCENTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo un docente puede consultar informes pedagógicos"
            );
        }
    }
}
