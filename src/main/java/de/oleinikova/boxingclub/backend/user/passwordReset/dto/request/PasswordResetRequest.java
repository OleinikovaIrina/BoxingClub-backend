package de.oleinikova.boxingclub.backend.user.passwordReset.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(

        @NotBlank(message = "{passwordResetToken.notBlank}")
        String passwordResetToken,
        @NotBlank(message = "{user.password.notBlank}")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$",
                message = "{user.password.pattern}"
        )
        @Schema(
                description = "new User password",
                example = "Box_club2025!"
        )
        String password
) {
}
