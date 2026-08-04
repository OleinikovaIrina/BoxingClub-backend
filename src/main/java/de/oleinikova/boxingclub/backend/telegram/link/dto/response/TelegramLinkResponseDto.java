package de.oleinikova.boxingclub.backend.telegram.link.dto.response;

import java.time.LocalDateTime;

public record TelegramLinkResponseDto(
        String telegramUrl,
        LocalDateTime expiresAt
) {
}