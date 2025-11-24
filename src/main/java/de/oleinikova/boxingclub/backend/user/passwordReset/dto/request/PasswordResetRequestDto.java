package de.oleinikova.boxingclub.backend.user.passwordReset.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDto (
        @Email(message = "{user.email.invalid}")
        @NotBlank(message = "{user.email.notBlank}")
        @Schema(
                description = "new User email",
                example = "test_box@box.de"
        )
        String email
)
{

}
