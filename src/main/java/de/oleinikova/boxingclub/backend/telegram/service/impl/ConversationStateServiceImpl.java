package de.oleinikova.boxingclub.backend.telegram.service.impl;

import de.oleinikova.boxingclub.backend.telegram.model.ConversationState;
import de.oleinikova.boxingclub.backend.telegram.service.interfaces.ConversationStateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class ConversationStateServiceImpl implements ConversationStateService {

    private final Map<Long, ConversationState> conversations =
            new ConcurrentHashMap<>();

    private final Map<Long, List<UUID>> availableSessions =
            new ConcurrentHashMap<>();

    private final Map<Long, List<UUID>> availableBookings =
            new ConcurrentHashMap<>();

    @Override
    public void setState(Long chatId, ConversationState state) {
        conversations.put(chatId, state);
    }

    @Override
    public ConversationState getState(Long chatId) {
        return conversations.get(chatId);
    }

    @Override
    public void saveAvailableSessions(Long chatId, List<UUID> sessions) {
        availableSessions.put(chatId, sessions);
    }

    @Override
    public UUID getSelectedSessionId(Long chatId, Integer number) {

        List<UUID> sessions = availableSessions.get(chatId);

        if (sessions == null) {
            return null;
        }

        if (number < 1 || number > sessions.size()) {
            return null;
        }

        return sessions.get(number - 1);
    }

    @Override
    public void clearState(Long chatId) {
        conversations.remove(chatId);
        availableSessions.remove(chatId);
        availableBookings.remove(chatId);
    }

    @Override
    public void saveAvailableBookings(Long chatId, List<UUID> bookingIds) {
        availableBookings.put(chatId, bookingIds);
    }

    @Override
    public UUID getSelectedBookingId(Long chatId, int number) {

        List<UUID> bookings = availableBookings.get(chatId);

        if (bookings == null) {
            return null;
        }

        if (number < 1 || number > bookings.size()) {
            return null;
        }

        return bookings.get(number - 1);
    }
}