package de.oleinikova.boxingclub.backend.session.dto.response;

import de.oleinikova.boxingclub.backend.session.entity.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponseDto(
        @Schema(
                description = "ID of the booking",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID bookingId,

        @Schema(
                description = "ID of the session",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID sessionId,

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
                description = "Session type: GROUP, INDIVIDUAL",
                example = "GROUP"
        )
        SessionType type,

        @Schema(
                description = "Trainer last name",
                example = "Tyson"
        )
        String trainerLastName

) {
}