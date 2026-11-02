package com.example.mpaz_pro_6springboot.common.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Respuesta uniforme de error de la API.
 *
 * <p>{@code fields} solo se incluye cuando hay errores de validacion por campo;
 * en el resto de casos se omite del JSON.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fields
) {

    public static ApiError de(HttpStatus estado, String message, String path) {
        return new ApiError(Instant.now(), estado.value(), estado.getReasonPhrase(), message, path, null);
    }

    public static ApiError de(HttpStatus estado, String message, String path,
            Map<String, String> fields) {
        return new ApiError(Instant.now(), estado.value(), estado.getReasonPhrase(), message, path, fields);
    }
}
