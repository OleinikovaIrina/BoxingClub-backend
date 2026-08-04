package de.oleinikova.boxingclub.backend.telegram.link.service.interfaces;

import de.oleinikova.boxingclub.backend.telegram.link.dto.response.TelegramLinkResponseDto;

public interface TelegramLinkService {

    TelegramLinkResponseDto createLink(String email);

    String completeLink(
            String rawToken,
            Long telegramChatId
    );
}