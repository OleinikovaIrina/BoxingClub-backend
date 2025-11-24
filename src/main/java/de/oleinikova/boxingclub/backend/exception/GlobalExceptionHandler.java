package de.oleinikova.boxingclub.backend.exception;

import de.oleinikova.boxingclub.backend.common.error.response.ErrorResponseDto;
import de.oleinikova.boxingclub.backend.common.error.response.ValidationErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Global exception handler with consistent JSON error responses.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    // --- 400: Bean Validation on @RequestBody ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        // глобальные ошибки (без конкретного поля)
        List<String> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(err -> err.getDefaultMessage())
                .toList();

        List<ValidationErrorDto> validation = new ArrayList<>();
        fieldErrors.forEach((f, msgs) -> validation.add(new ValidationErrorDto(f, msgs)));
        if (!globalErrors.isEmpty()) {
            validation.add(new ValidationErrorDto(null, globalErrors));
        }

        ErrorResponseDto body = error(HttpStatus.BAD_REQUEST, "Validation failed", validation, request);
        log.warn("400 Validation failed: {}", ExceptionUtils.getMessage(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // --- 400: @Validated on params/path ---
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, List<String>> byPath = new LinkedHashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : null;
            byPath.computeIfAbsent(path, k -> new ArrayList<>()).add(v.getMessage());
        }
        List<ValidationErrorDto> validation = byPath.entrySet().stream()
                .map(e -> new ValidationErrorDto(e.getKey(), e.getValue()))
                .toList();

        ErrorResponseDto body = error(HttpStatus.BAD_REQUEST, "Constraint violation", validation, request);
        log.warn("400 Constraint violation: {}", ExceptionUtils.getMessage(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // --- 400: malformed JSON / wrong type / missing param ---
    @ExceptionHandler({
            org.springframework.http.converter.HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequest(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = error(HttpStatus.BAD_REQUEST, "Bad request", List.of(), request);
        log.warn("400 Bad request: {}", ExceptionUtils.getMessage(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                List.of(),
                request
        );

        log.warn("400 Invalid token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    // --- 404 ---
    @ExceptionHandler({  NoResourceFoundException.class,NoHandlerFoundException.class, ResourceNotFoundException.class })
    public ResponseEntity<ErrorResponseDto> handleNotFound(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = error(HttpStatus.NOT_FOUND, "Not found", List.of(), request);
        log.warn("404 Not found: {}", ExceptionUtils.getMessage(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // --- 409: DB constraints / unique index ---
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = error(HttpStatus.CONFLICT, "Conflict", List.of(), request);
        log.warn("409 Conflict: {}", ExceptionUtils.getRootCauseMessage(ex));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // --- 410: expired domain token (e.g., password reset) ---
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponseDto> handleGone(
            TokenExpiredException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = error(HttpStatus.GONE, ex.getMessage(), List.of(), request);
        log.warn("410 Gone: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(body);
    }


    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ErrorResponseDto> handleRestApiException(
            RestApiException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDto body = error(ex.getHttpStatus(), ex.getMessage(), List.of(), request);
        log.warn("{} {}", ex.getHttpStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    // --- 500 fallback ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUncaught(Exception ex, HttpServletRequest request) {
        ErrorResponseDto body = error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", List.of(), request);
        log.error("500 Unhandled exception: {}", ExceptionUtils.getMessage(ex), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // helper
    private ErrorResponseDto error(HttpStatus status, String message, List<ValidationErrorDto> details, HttpServletRequest req) {
        return new ErrorResponseDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details,
                req.getRequestURI()
        );
    }
}