package com.example.mpaz_pro_6springboot.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Entrada invalida o regla de negocio incumplida. Se traduce a HTTP 400
 * cuando exista el manejo global.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
