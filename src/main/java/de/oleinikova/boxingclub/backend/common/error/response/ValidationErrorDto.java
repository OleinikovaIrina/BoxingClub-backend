package de.oleinikova.boxingclub.backend.common.error.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** Field-level validation error details */
@Schema(description = "Details of a validation error for a specific field")
public record ValidationErrorDto(
        @Schema(description = "Field name", example = "email")
        String field,

        @Schema(description = "Validation messages", example = "[\"must be a well-formed email address\"]")
        List<String> messages
) {}
