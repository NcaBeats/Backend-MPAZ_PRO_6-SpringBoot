package com.example.mpaz_pro_6springboot.common.util;

import com.example.mpaz_pro_6springboot.common.exception.ReglaNegocioException;

/**
 * Validaciones simples compartidas por la capa de servicio.
 */
public final class Validaciones {

    private Validaciones() {
    }

    public static void requerido(Object valor, String campo) {
        if (valor == null) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
    }

    public static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    public static String textoOpcional(String valor) {
        return valor == null ? null : valor.trim();
    }
}
