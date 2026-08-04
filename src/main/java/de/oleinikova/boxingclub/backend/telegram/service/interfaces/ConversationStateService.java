package de.oleinikova.boxingclub.backend.telegram.service.interfaces;

import de.oleinikova.boxingclub.backend.telegram.model.ConversationState;

import java.util.List;
import java.util.UUID;

public interface ConversationStateService {

    void setState(Long chatId, ConversationState state);

    ConversationState getState(Long chatId);

    void clearState(Long chatId);

    void saveAvailableSessions(Long chatId, List<UUID> sessions);

    UUID getSelectedSessionId(Long chatId, Integer number);

    void saveAvailableBookings(Long chatId, List<UUID> bookingIds);

    UUID getSelectedBookingId(Long chatId, int number);
}
