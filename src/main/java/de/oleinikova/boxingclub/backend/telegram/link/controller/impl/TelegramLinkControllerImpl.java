package de.oleinikova.boxingclub.backend.telegram.link.controller.impl;

import de.oleinikova.boxingclub.backend.telegram.link.controller.interfaces.TelegramLinkApi;
import de.oleinikova.boxingclub.backend.telegram.link.dto.response.TelegramLinkResponseDto;
import de.oleinikova.boxingclub.backend.telegram.link.service.interfaces.TelegramLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramLinkControllerImpl implements TelegramLinkApi {

    private final TelegramLinkService telegramLinkService;

    @Override
    public TelegramLinkResponseDto createLink(Authentication authentication) {

        String email = authentication.getName();

        return telegramLinkService.createLink(email);

    }
}
