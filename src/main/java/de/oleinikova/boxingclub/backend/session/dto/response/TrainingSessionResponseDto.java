package de.oleinikova.boxingclub.backend.session.dto.response;

import de.oleinikova.boxingclub.backend.session.entity.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record TrainingSessionResponseDto(
        @Schema(
                description = "Session ID",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID id,

        @Schema(
                description = "Session title",
                example = "Boxing Basics"
        )
        String title,

        @Schema(
                description = "Session date and time",
                example = "2026-04-20T20:00:00"
        )
        LocalDateTime dateTime,

        @Schema(
                description = "Session duration",
                example = "60"
        )
        Integer durationMinutes,


        @Schema(
                description = "Session type: GROUP, INDIVIDUAL",
                example = "GROUP"
        )
        SessionType type,

        @Schema(
                description = "Max participants",
                example = "8"
        )
        Integer maxParticipants,

        @Schema(
                description = "Trainer last name",
                example = "Tyson"
        )
        String trainerLastName,

        @Schema(
                description = "Trainer id",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID trainerId,

        @Schema(
                description = "Number of available slots ",
                example = "5"
        )
        Integer availableSlots

) {
}

