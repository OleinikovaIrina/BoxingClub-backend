package de.oleinikova.boxingclub.backend.telegram.service.impl;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import de.oleinikova.boxingclub.backend.session.entity.Booking;
import de.oleinikova.boxingclub.backend.session.entity.TrainingSession;
import de.oleinikova.boxingclub.backend.session.persistence.BookingRepository;
import de.oleinikova.boxingclub.backend.session.persistence.TrainingSessionRepository;
import de.oleinikova.boxingclub.backend.telegram.link.service.interfaces.TelegramLinkService;
import de.oleinikova.boxingclub.backend.telegram.model.ConversationState;
import de.oleinikova.boxingclub.backend.telegram.service.interfaces.ConversationStateService;
import de.oleinikova.boxingclub.backend.telegram.service.interfaces.TelegramBotService;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramBotServiceImpl implements TelegramBotService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final TelegramBookingTransactionService telegramBookingTransactionService;
    private final ConversationStateService conversationStateService;
    private final AppUserRepository appUserRepository;
    private final BookingRepository bookingRepository;
    private final TelegramLinkService telegramLinkService;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    @Transactional
    public String handleMessage(Long chatId, String message) {

        message = message.trim();

        if (message.startsWith("/start ")) {

            String rawToken = message
                    .substring("/start ".length())
                    .trim();

            conversationStateService.clearState(chatId);

            try {
                return telegramLinkService.completeLink(
                        rawToken,
                        chatId
                );

            } catch (DataIntegrityViolationException e) {

                return telegramLinkService.completeLink(
                        rawToken,
                        chatId
                );
            }
        }

        ConversationState state = conversationStateService.getState(chatId);


        if (state == ConversationState.WAITING_FOR_SESSION_ID) {
            return handleSessionSelection(chatId, message);
        }

        if (state == ConversationState.WAITING_FOR_CANCELLATION_ID) {
            return handleCancellationSelection(chatId, message);
        }

        if (requiresLinkedAccount(message)
                && appUserRepository.findByTelegramChatId(chatId).isEmpty()) {

            return """
                    Telegram is not linked to your account.
                    Use /start first.
                    """;
        }

        return switch (message) {


            case "/start" -> handleStart(chatId);

            case "/help" -> buildHelp();

            case "/trainings" -> handleTrainings();

            case "/slots" -> handleSlots();

            case "/book" -> handleBook(chatId);

            case "/my" -> handleMyTrainings(chatId);

            case "/cancel" -> handleCancel(chatId);

            default -> """
                    Unknown command.
                    Type /help
                    """;
        };

    }

    private String handleStart(Long chatId) {

        Optional<AppUser> user =
                appUserRepository.findByTelegramChatId(chatId);

        if (user.isPresent()) {
            return """
                    Welcome back, %s!

                    %s
                    """.formatted(
                    user.get().getFirstName(),
                    buildHelp()
            );
        }

        return """
                Welcome to BoxingClub Bot 🥊

                To link Telegram securely:

                1. Log in to your BoxingClub account.
                2. Open your profile.
                3. Click "Connect Telegram".
                4. Open the generated Telegram link.
                """;
    }

    private String handleTrainings() {


        List<TrainingSession> trainings = trainingSessionRepository
                .findAllByCancelledFalseAndStartTimeAfterOrderByStartTimeAsc(LocalDateTime.now());

        if (trainings.isEmpty()) {
            return "No upcoming trainings.";
        }

        StringBuilder result = new StringBuilder("Upcoming trainings:\n\n");

        trainings.stream()
                .limit(10)
                .forEach(session ->
                        result.append(formatTraining(session)));

        return result.toString();
    }

    private String buildHelp() {

        return """
                Available commands:

                /start
                /help
                /trainings
                /slots
                /book
                /my
                /cancel
                """;
    }

    private String handleSlots() {

        List<TrainingSession> availableSessions = trainingSessionRepository
                .findAllByCancelledFalseAndStartTimeAfterOrderByStartTimeAsc(LocalDateTime.now())
                .stream()
                .filter(session -> calculateAvailableSlots(session) > 0)
                .limit(10)
                .toList();

        if (availableSessions.isEmpty()) {
            return "No trainings with available slots.";
        }


        StringBuilder result =
                new StringBuilder("Available trainings:\n\n");

        availableSessions.forEach(session ->
                result.append(formatTraining(session)));

        return result.toString();
    }


    private String handleBook(Long chatId) {

        List<TrainingSession> trainings = trainingSessionRepository
                .findAllByCancelledFalseAndStartTimeAfterOrderByStartTimeAsc(
                        LocalDateTime.now()
                )
                .stream()
                .filter(session -> calculateAvailableSlots(session) > 0)
                .limit(10)
                .toList();

        if (trainings.isEmpty()) {
            return "No trainings available.";
        }

        List<UUID> sessions = trainings.stream()
                .map(TrainingSession::getId)
                .toList();

        conversationStateService.saveAvailableSessions(chatId, sessions);

        conversationStateService.setState(chatId, ConversationState.WAITING_FOR_SESSION_ID);

        StringBuilder result = new StringBuilder("""
                     Select training:

                """);

        for (int i = 0; i < trainings.size(); i++) {

            TrainingSession session = trainings.get(i);

            result.append("""
                     %d. %s
                     📅 %s
                     👤 %s

                    """.formatted(
                    i + 1,
                    session.getTitle(),
                    session.getStartTime().format(FORMATTER),
                    session.getTrainer().getLastName()

            ));
        }

        result.append("""

                Enter the training number.
                """);

        return result.toString();
    }

    private String handleCancellationSelection(Long chatId, String message) {

        Integer number;
        try {
            number = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            return "Please enter a valid training number.";
        }

        UUID bookingId = conversationStateService.getSelectedBookingId(chatId, number);

        if (bookingId == null) {

            return """
                    Invalid training number.
                    Please choose a number from the list.
                    """;
        }
        AppUser user = appUserRepository.findByTelegramChatId(chatId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Telegram user not linked."
                        )
                );
        try {
            telegramBookingTransactionService.cancelBooking(
                    bookingId,
                    user.getEmail());
            conversationStateService.clearState(chatId);
            return """
                    ✅ Booking cancelled successfully.

                    To cancel another training, use /cancel again.
                    """;

        } catch (RestApiException e) {
            return e.getMessage();
        }
    }

    private String handleSessionSelection(Long chatId, String message) {

        Integer number;

        try {
            number = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            return "Please enter a valid training number.";
        }

        UUID sessionId = conversationStateService.getSelectedSessionId(chatId, number);

        if (sessionId == null) {
            return """
                    Invalid training number.
                    Please choose a number from the list.
                    """;
        }

        AppUser user = appUserRepository.findByTelegramChatId(chatId)
                .orElseThrow(() ->
                        new IllegalStateException("Telegram user not linked."));

        try {
            telegramBookingTransactionService.createBooking(
                    sessionId,
                    user.getEmail());

            conversationStateService.clearState(chatId);

            return "✅ Training booked successfully.";
        } catch (RestApiException e) {
            return e.getMessage();
        }
    }

    private String handleMyTrainings(Long chatId) {

        List<Booking> bookings = getUpcomingBookings(chatId);

        if (bookings.isEmpty()) {
            return """
                    You have no upcoming trainings.

                    Use /book to book a training.
                    """;
        }
        StringBuilder result = new StringBuilder("Your upcoming trainings:\n\n");

        bookings.forEach(booking -> result.append(formatBooking(booking)));

        return result.toString();
    }


    private String handleCancel(Long chatId) {


        List<Booking> bookings = getUpcomingBookings(chatId);

        if (bookings.isEmpty()) {
            return "You have no upcoming trainings to cancel.";
        }

        List<UUID> bookingIds = bookings.stream()
                .map(Booking::getId)
                .toList();

        conversationStateService.saveAvailableBookings(chatId, bookingIds);
        conversationStateService.setState(chatId, ConversationState.WAITING_FOR_CANCELLATION_ID);

        StringBuilder result = new StringBuilder("""
                Select training to cancel:

                """);

        for (int i = 0; i < bookings.size(); i++) {
            TrainingSession session = bookings.get(i).getSession();

            result.append("""
                    %d. %s
                    📅 %s
                    👤 %s

                    """.formatted(
                    i + 1,
                    session.getTitle(),
                    session.getStartTime().format(FORMATTER),
                    session.getTrainer().getLastName()
            ));
        }
        result.append("Enter the training number.");

        return result.toString();

    }

    private List<Booking> getUpcomingBookings(Long chatId) {

        return bookingRepository
                .findAllByUser_TelegramChatIdAndCancelledFalseAndSession_StartTimeAfterOrderBySession_StartTimeAsc(
                        chatId,
                        LocalDateTime.now()
                )
                .stream()
                .limit(10)
                .toList();
    }


    private boolean requiresLinkedAccount(String message) {

        return message.equals("/book")
                || message.equals("/my")
                || message.equals("/cancel");
    }


    private int calculateAvailableSlots(TrainingSession session) {

        int activeBookings =
                (int) bookingRepository.countBySession_IdAndCancelledFalse(session.getId());

        return session.getMaxParticipants() - activeBookings;
    }


    private String formatTraining(TrainingSession session) {


        return """
                🥊 %s
                📅 %s
                👤 %s
                🟢 Available: %d

                """
                .formatted(
                        session.getTitle(),
                        session.getStartTime().format(FORMATTER),
                        session.getTrainer().getLastName(),
                        calculateAvailableSlots(session)
                );
    }

    private String formatBooking(Booking booking) {

        TrainingSession session = booking.getSession();

        return """
                🥊 %s
                📅 %s
                👤 %s

                """

                .formatted(
                        session.getTitle(),
                        session.getStartTime().format(FORMATTER),
                        session.getTrainer().getLastName()
                );
    }
}
