package com.example.mpaz_pro_6springboot.common.security;

import com.example.mpaz_pro_6springboot.identidad.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;

/**
 * Acceso al usuario autenticado desde servicios y controladores.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<Usuario> currentUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(details.getUsuario());
        }
        return Optional.empty();
    }

    public static Usuario requireUsuario() {
        return currentUsuario().orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "No autenticado"));
    }

    public static boolean hasRole(String role) {
        return currentUsuario()
                .map(Usuario::getRol)
                .map(rol -> rol.getNombre() != null
                        && rol.getNombre().toUpperCase(Locale.ROOT)
                        .equals(role.toUpperCase(Locale.ROOT)))
                .orElse(false);
    }

    public static boolean isEstudiante() {
        return hasRole("ESTUDIANTE");
    }

    public static boolean isDocente() {
        return hasRole("DOCENTE");
    }

    public static boolean isUtp() {
        return hasRole("UTP");
    }

    /**
     * Un estudiante solo puede acceder a sus propios recursos.
     */
    public static void requireEstudiantePropietario(Long propietarioId, String recurso) {
        if (!isEstudiante()) {
            return;
        }
        Usuario actual = requireUsuario();
        if (propietarioId == null || !propietarioId.equals(actual.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Un estudiante solo puede acceder a " + recurso + " propios");
        }
    }
}
