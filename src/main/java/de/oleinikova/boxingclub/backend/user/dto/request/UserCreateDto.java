package de.oleinikova.boxingclub.backend.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserCreateDto(


        @NotBlank(message = "{user.firstName.notBlank}")
        @Schema(
                description = "First name",
                example = "Iryna")
        String firstName,

        @NotBlank(message = "{user.lastName.notBlank}")
        @Schema(
                description = "Last name",
                example = "Oleinikova")
        String lastName,

        @Email(message = "{user.email.invalid}")
        @NotBlank(message = "{user.email.notBlank}")
        @Schema(
                description = "new User email",
                example = "test_box@box.de"
        )
        String email,
        @NotBlank(message = "{user.password.notBlank}")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$",
                message = "{user.password.pattern}"
        )
        @Schema(
                description = "new User password",
                example = " "
        )
        String password
        )
{

}
