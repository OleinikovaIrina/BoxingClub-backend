package de.oleinikova.boxingclub.backend.telegram.service.interfaces;

public interface TelegramBotService {

    String handleMessage(Long chatId, String message);
}
