package de.oleinikova.boxingclub.backend.common.error.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/** Standard API error response */
@Schema(description = "Standard API error response")
public record ErrorResponseDto(
        @Schema(description = "Timestamp when the error occurred", example = "2025-09-13T20:15:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "409")
        int status,

        @Schema(description = "HTTP status reason phrase", example = "Conflict")
        String error,

        @Schema(description = "Human-readable error message", example = "Validation failed")
        String message,

        @Schema(description = "Validation errors (if any)", implementation = ValidationErrorDto.class)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<ValidationErrorDto> errors,

        @Schema(description = "Request path", example = "/api/users")
        String path
) {}
