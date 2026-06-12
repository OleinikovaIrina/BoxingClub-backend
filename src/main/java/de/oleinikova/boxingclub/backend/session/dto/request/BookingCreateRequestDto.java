package de.oleinikova.boxingclub.backend.session.dto.request;

import java.util.UUID;

public record BookingCreateRequestDto(
        UUID sessionId
)
{
}
