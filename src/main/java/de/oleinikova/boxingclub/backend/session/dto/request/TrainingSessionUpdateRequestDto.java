package de.oleinikova.boxingclub.backend.session.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TrainingSessionUpdateRequestDto(

        @NotBlank(message = "{session.title.notBlank}")
        @Schema(
                description = "Session title",
                example = "Boxing Basics"
        )
        String title,

        @NotNull(message = "{session.dateTime.notNull}")
        @Schema(
                description = "Session date and time",
                example = "2026-04-20T20:00:00"
        )
        LocalDateTime dateTime,

        @NotNull(message = "{session.duration.notNull}")
        @Min(value = 30, message = "{session.duration.min}")
        @Max(value = 180, message = "{session.duration.max}")
        @Schema(
                description = "Session duration",
                example = "60"
        )
        Integer durationMinutes,

        @NotNull(message = "{session.maxParticipants.notNull}")
        @Min(value = 1, message = "{session.maxParticipants.min}")
        @Max(value = 12, message = "{session.maxParticipants.max}")
        @Schema(
                description = "Max participants",
                example = "8"
        )
        Integer maxParticipants,

        @NotNull(message = "{session.trainerId.notNull}")
        UUID trainerId

) {
}
