package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.common.enums.EstadoContenido;
import com.example.mpaz_pro_6springboot.common.enums.Rol;
import com.example.mpaz_pro_6springboot.curriculo.unidad.model.Unidad;
import com.example.mpaz_pro_6springboot.docencia.docencia.repository.DocenteCursoAsignaturaRepository;
import com.example.mpaz_pro_6springboot.identidad.usuario.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reglas de autoría del catalogo curricular.
 *
 * <p>La UTP conserva el acceso completo. Un docente solo puede crear material
 * dentro de unidades de sus asignaturas y mientras la unidad este en
 * {@code BORRADOR}. El envio a revision es el paso que habilita a la UTP para
 * autorizar o publicar.</p>
 */
@Component
@RequiredArgsConstructor
public class CurriculoAuthoringPolicy {

    private final DocenteCursoAsignaturaRepository docenciaRepository;

    /** Valida que el usuario pueda crear o modificar material de la unidad. */
    public void assertCanAuthor(Usuario usuario, Unidad unidad) {
        if (usuario.getRol() == Rol.UTP) {
            return;
        }

        if (usuario.getRol() != Rol.DOCENTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo la UTP y los docentes pueden crear material curricular"
            );
        }

        if (unidad.getEstado() != EstadoContenido.BORRADOR) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se puede modificar material de una unidad en borrador"
            );
        }

        boolean ownsSubject = docenciaRepository.existsByDocenteIdAndAsignaturaId(
                usuario.getId(),
                unidad.getAsignatura().getId()
        );
        if (!ownsSubject) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo puedes crear material de tus asignaturas"
            );
        }
    }

    /** Valida que el docente pueda enviar su unidad en borrador a revision. */
    public void assertCanSubmitReview(Usuario usuario, Unidad unidad) {
        if (usuario.getRol() != Rol.DOCENTE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo un docente puede enviar una unidad a revision"
            );
        }

        if (unidad.getEstado() != EstadoContenido.BORRADOR) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se puede enviar a revision una unidad en borrador"
            );
        }

        boolean ownsSubject = docenciaRepository.existsByDocenteIdAndAsignaturaId(
                usuario.getId(),
                unidad.getAsignatura().getId()
        );
        if (!ownsSubject) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo puedes enviar unidades de tus asignaturas"
            );
        }
    }
}
