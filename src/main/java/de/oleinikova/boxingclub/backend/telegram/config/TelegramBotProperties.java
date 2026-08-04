package de.oleinikova.boxingclub.backend.telegram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record TelegramBotProperties(

        String username,
        String token,
        long linkTokenExpirationMinutes
) {
}
