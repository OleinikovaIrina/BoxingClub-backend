package de.oleinikova.boxingclub.backend.telegram.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Configuration
@EnableConfigurationProperties(TelegramBotProperties.class)
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramConfig {

    @Bean
    public OkHttpTelegramClient telegramClient(
            TelegramBotProperties properties
    ) {
        return new OkHttpTelegramClient(
                properties.token()
        );
    }
}
