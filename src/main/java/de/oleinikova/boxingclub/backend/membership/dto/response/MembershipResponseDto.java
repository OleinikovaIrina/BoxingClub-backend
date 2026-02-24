package de.oleinikova.boxingclub.backend.membership.dto.response;

import de.oleinikova.boxingclub.backend.membership.entity.MembershipDuration;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipStatus;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record MembershipResponseDto
        (
                @Schema(
                        description = "ID of the membership",
                        example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                )
                UUID membershipId,

                @Schema(
                        description = "ID of the user who owns the membership",
                        example = "1c9e1f92-4c0f-4b8a-9a4e-9c5a9b9f0a12"
                )
                UUID userId,                @Schema
                        (description = "User first name",
                                example = "Iryna")
                String firstName,

                @Schema
                        (description = "User last name",
                        example = "Oleinikova")
                String lastName,

                @Schema(
                        description = "Type of membership",
                        example = "ADULT"
                )
                MembershipType type,

                @Schema(
                        description = "Subscription period",
                        example = "MONTHLY"
                )
                MembershipDuration duration,

                @Schema(
                        description = "Application status",
                        example = "PENDING"
                )
                MembershipStatus status,

                @Schema(
                        description = "Date when the membership becomes active. " +
                                "Set when the administrator confirms activation. " +
                                "Null if the membership is not yet activated.",
                        example = "2026-01-01"
                )
                LocalDate startDate,

                @Schema(
                        description = "Date when the membership expires based on the selected duration. " +
                                "Null if the membership is not yet activated.",
                        example = "2026-02-01"
                )
                LocalDate endDate,

                @Schema(
                        description = "Indicates whether the membership is currently active. " +
                                "True only if status is ACTIVE and current date is within the subscription period.",
                        example = "true"
                )
                boolean active
        ) {
}

