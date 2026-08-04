package de.oleinikova.boxingclub.backend.telegram.service.impl;

import de.oleinikova.boxingclub.backend.telegram.service.interfaces.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramMessageSenderImpl implements TelegramMessageSender {

    private final OkHttpTelegramClient telegramClient;

    @Override
    public boolean sendMessage(long chatId, String text) {

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            telegramClient.execute(message);
            return true;

        } catch (TelegramApiException e) {
            log.error(
                    "Failed to send Telegram message to chat {}",
                    chatId,
                    e
            );

            return false;
        }
    }
}
