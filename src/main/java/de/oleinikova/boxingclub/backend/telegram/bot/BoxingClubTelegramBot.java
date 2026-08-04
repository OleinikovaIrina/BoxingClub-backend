package de.oleinikova.boxingclub.backend.telegram.bot;


import de.oleinikova.boxingclub.backend.telegram.service.interfaces.TelegramBotService;
import de.oleinikova.boxingclub.backend.telegram.service.interfaces.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;


@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class BoxingClubTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramBotService telegramBotService;
    private final TelegramMessageSender telegramMessageSender;

    @Override
    public void consume(Update update) {

        if (!update.hasMessage()) {
            return;
        }

        if (!update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        String response = telegramBotService.handleMessage(chatId, text);

        telegramMessageSender.sendMessage(chatId, response);

        log.info(
                "Telegram message received from chat {}",
                chatId
        );
    }
}
