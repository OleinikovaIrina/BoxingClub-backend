package de.oleinikova.boxingclub.backend.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record TrainerResponseDto(

        @Schema(
                description = "User ID",
                example = "e3c9cbd9-75c3-4564-883a-7e6a6d7c611b")
        UUID id,

        @Schema(
                description = "Trainer last name",
                example = "Tyson"
        )
        String lastName

) {
}
