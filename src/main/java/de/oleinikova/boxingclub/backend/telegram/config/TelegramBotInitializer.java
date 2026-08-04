package de.oleinikova.boxingclub.backend.telegram.config;

import de.oleinikova.boxingclub.backend.telegram.bot.BoxingClubTelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramBotInitializer {

    private final TelegramBotProperties properties;
    private final BoxingClubTelegramBot boxingClubTelegramBot;

    @PostConstruct
    public void init() {

        try {

           log.info("Telegram bot initialization started");

            TelegramBotsLongPollingApplication application =
                    new TelegramBotsLongPollingApplication();

            application.registerBot(
                    properties.token(),
                    boxingClubTelegramBot
            );

            log.info("Telegram bot registered successfully");

        } catch (TelegramApiException e) {

            throw new RuntimeException(
                    "Failed to register Telegram bot",
                    e
            );
        }
    }
}