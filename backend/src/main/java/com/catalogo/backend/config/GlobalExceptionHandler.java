package com.catalogo.backend.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error(HttpStatus.BAD_REQUEST, "Datos inválidos", "Revisa los campos enviados", request, fields,
                exception);
    }

    @ExceptionHandler({ ConstraintViolationException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class, MissingServletRequestPartException.class,
            IllegalArgumentException.class })
    public org.springframework.http.ResponseEntity<ApiError> handleBadRequest(
            Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Solicitud inválida", safeMessage(exception), request, Map.of(),
                exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleNotFound(
            EntityNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "Recurso no encontrado", exception.getMessage(), request, Map.of(),
                exception);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleConflict(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Conflicto de datos",
                "El registro no se puede guardar porque viola una restricción de datos", request, Map.of(), exception);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleTransaction(
            TransactionSystemException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "No se pudo guardar",
                "Los datos no cumplen las restricciones de la base de datos",
                request, Map.of(), exception);
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<ApiError> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error inesperado. Revisa los logs del backend", request, Map.of(), exception);
    }

    private org.springframework.http.ResponseEntity<ApiError> error(
            HttpStatus status, String error, String message, HttpServletRequest request,
            Map<String, String> fields, Exception exception) {
        if (status.is5xxServerError()) {
            log.error("API error status={} method={} path={} message={}", status.value(), request.getMethod(),
                    request.getRequestURI(), message, exception);
        } else {
            log.error("API client error status={} method={} path={} message={}", status.value(), request.getMethod(),
                    request.getRequestURI(), message);
        }
        ApiError body = new ApiError(Instant.now(), status.value(), error, message, request.getRequestURI(), fields);
        return org.springframework.http.ResponseEntity.status(status).body(body);
    }

    private String safeMessage(Exception exception) {
        return exception.getMessage() == null ? "El cuerpo de la solicitud no es válido" : exception.getMessage();
    }
}
