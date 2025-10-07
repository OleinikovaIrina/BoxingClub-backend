package de.oleinikova.boxingclub.backend.user.dto.response;

import de.oleinikova.boxingclub.backend.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserCreateResponseDto (

        @Schema(
                description = "User ID",
                example = "e3c9cbd9-75c3-4564-883a-7e6a6d7c611b")
        UUID id,

        @Schema(
                description = "User's email  address",
                example = "maik@taison.com"
        )
        String email,

        @Schema(
                description = "Role granted to the user",
                example = "ROLE_USER",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Role role,

        @Schema(
                description = "Flag indicating if the confirmation email was resent. True if the email was resent due to unconfirmed status; false if sent initially.",
                example = "true",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        boolean confirmationResent) {
}



