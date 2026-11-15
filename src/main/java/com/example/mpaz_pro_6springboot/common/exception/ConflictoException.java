package com.example.mpaz_pro_6springboot.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Conflicto con el estado actual de los datos (duplicados, reglas de estado,
 * limites alcanzados). Se traduce a HTTP 409 cuando exista el manejo global.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
