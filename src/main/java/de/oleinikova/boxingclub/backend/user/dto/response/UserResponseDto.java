package de.oleinikova.boxingclub.backend.user.dto.response;

import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User data returned by the API")
public record UserResponseDto(
        @Schema(
                description = "User's email  address",
                example = "maik@taison.com"
        )
        String email,

        @Schema(
                description = "Role assigned to the user",
                example = "ROLE_USER",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        Role role,

        @Schema(
                description = "Confirmation status of the user account",
                example = "UNCONFIRMED",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        ConfirmationStatus confirmationStatus
) {
}
