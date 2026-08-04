package de.oleinikova.boxingclub.backend.telegram.service.interfaces;

public interface TelegramMessageSender {

    boolean sendMessage(long chatId, String text);
}
