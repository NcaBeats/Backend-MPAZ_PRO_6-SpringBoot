package com.example.mpaz_pro_6springboot.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Traduce las excepciones del dominio y de validacion a respuestas HTTP
 * uniformes ({@link ApiError}).
 *
 * <p>Los servicios siguen siendo los dueños de las reglas de negocio. Este
 * manejador solo define el contrato de salida: {@code 400} para entradas
 * invalidas o reglas incumplidas, {@code 404} para recursos inexistentes y
 * {@code 409} para conflictos de estado o de integridad referencial.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError recursoNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return ApiError.de(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError reglaNegocio(ReglaNegocioException ex, HttpServletRequest request) {
        return ApiError.de(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConflictoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflicto(ConflictoException ex, HttpServletRequest request) {
        return ApiError.de(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return ApiError.de(HttpStatus.BAD_REQUEST,
                "La peticion contiene campos invalidos", request.getRequestURI(), fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError restriccion(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> fields.putIfAbsent(
                violation.getPropertyPath().toString(), violation.getMessage()));
        return ApiError.de(HttpStatus.BAD_REQUEST,
                "La peticion contiene parametros invalidos", request.getRequestURI(), fields);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError peticionInvalida(Exception ex, HttpServletRequest request) {
        return ApiError.de(HttpStatus.BAD_REQUEST,
                "La peticion no se pudo interpretar", request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        return ApiError.de(HttpStatus.CONFLICT,
                "La operacion viola una restriccion de integridad de datos",
                request.getRequestURI());
    }
}
