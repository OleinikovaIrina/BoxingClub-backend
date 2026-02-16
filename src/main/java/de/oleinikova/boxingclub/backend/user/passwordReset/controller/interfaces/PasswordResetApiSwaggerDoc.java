package de.oleinikova.boxingclub.backend.user.passwordReset.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.passwordReset.dto.request.PasswordResetRequest;
import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Swagger documentation for password reset token endpoints .
 */
@Tag(name = "Password reset ", description = "Endpoints for password reset ")
public interface PasswordResetApiSwaggerDoc {

    @Operation(summary = "Password reset token")
    ResponseEntity<Void> createPasswordResetToken(String email);

    @Operation(summary = "Validate password reset token")
    ResponseEntity<Boolean> validatePasswordResetToken(String tokenValue);

    @Operation(summary = " Reset  password ")
    ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request);

}