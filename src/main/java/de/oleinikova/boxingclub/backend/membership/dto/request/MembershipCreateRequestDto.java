package de.oleinikova.boxingclub.backend.membership.dto.request;

import de.oleinikova.boxingclub.backend.membership.entity.MembershipDuration;
import de.oleinikova.boxingclub.backend.membership.entity.MembershipType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record MembershipCreateRequestDto(

        @NotNull(message = "{membership.type.notNull}")
        @Schema(
                description = "Type of membership: ADULT, STUDENT, CHILD, FAMILY",
                example = "ADULT"
        )
        MembershipType type,

        @NotNull(message = "{membership.duration.notNull}")
        @Schema(
                description = "Type of duration: TRIAL, MONTHLY, YEARLY",
                example = "MONTHLY"
        )
        MembershipDuration duration,

        @NotBlank(message = "{membership.street.notBlank}")
        @Schema(
                description = "Street address",
                example = "Musterstraße 22"
        )
        String street,

        @NotBlank(message = "{membership.postalCode.notBlank}")
        @Pattern(regexp = "\\d{5}", message = "{membership.postalCode.pattern}")
        @Schema(
                description = "Postal code",
                example = "80331"
        )
        String postalCode,

        @NotBlank(message = "{membership.city.notBlank}")
        @Schema(
                description = "City",
                example = "Aschaffenburg"
        )
        String city,

        @Pattern(  regexp = "^[A-Za-z]{2}\\d{2}[A-Za-z0-9 ]{1,32}$", message = "{membership.iban.invalid}")
        @Schema(description =  "IBAN (stored). Spaces will be stripped and letters uppercased. Not returned in responses.",
                example = "DE89370400440532013000")
        String iban,

        @AssertTrue(message = "{membership.sepaConsent.required}")
        @Schema(
                description = "User consent to SEPA debit (required)",
                example = "true"
        )
        boolean consentToSepa,

        @AssertTrue(message = "{membership.dataPolicyConsent.required}")
        @Schema(
                description = "User agrees to processing of personal data",
                example = "true"
        )
        boolean consentToDataPolicy,

        @Schema(
                description = "Indicates whether user has discount (e.g. student/child)",
                example = "true")
        boolean hasDiscount

) {
}



