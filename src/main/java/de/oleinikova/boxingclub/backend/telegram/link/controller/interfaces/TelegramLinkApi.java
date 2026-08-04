package de.oleinikova.boxingclub.backend.telegram.link.controller.interfaces;

import de.oleinikova.boxingclub.backend.telegram.link.dto.response.TelegramLinkResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/user/telegram")
public interface TelegramLinkApi {

    @PostMapping("/link")
    @ResponseStatus(HttpStatus.CREATED)
    TelegramLinkResponseDto createLink(
            Authentication authentication
    );
}