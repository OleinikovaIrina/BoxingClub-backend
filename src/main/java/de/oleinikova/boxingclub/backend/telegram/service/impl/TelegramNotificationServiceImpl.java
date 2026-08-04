package de.oleinikova.boxingclub.backend.telegram.service.impl;

import de.oleinikova.boxingclub.backend.session.entity.Booking;
import de.oleinikova.boxingclub.backend.session.entity.TrainingSession;
import de.oleinikova.boxingclub.backend.session.persistence.BookingRepository;
import de.oleinikova.boxingclub.backend.telegram.service.interfaces.TelegramMessageSender;
import de.oleinikova.boxingclub.backend.telegram.service.interfaces.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramNotificationServiceImpl implements TelegramNotificationService {

    private static final ZoneId BERLIN_ZONE = ZoneId.of("Europe/Berlin");

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final BookingRepository bookingRepository;
    private final TelegramMessageSender telegramMessageSender;

    @Override
    @Transactional
    @Scheduled(
            cron = "0 * * * * *",
            zone = "Europe/Berlin"

    )
    public void sendTrainingReminders() {

        LocalDateTime now = LocalDateTime.now(BERLIN_ZONE);

        LocalDateTime from = now.plusMinutes(55);

        LocalDateTime to = now.plusMinutes(65);

        List<Booking> bookings =
                bookingRepository.findAllByCancelledFalseAndReminderSentFalseAndSession_CancelledFalseAndUser_TelegramChatIdIsNotNullAndSession_StartTimeBetween(from, to);

        for (Booking booking : bookings) {

            Long chatId =
                    booking.getUser().getTelegramChatId();

            String message = buildReminderMessage(booking);

            boolean sent = telegramMessageSender.sendMessage(chatId, message);

            if (sent) {
                booking.setReminderSent(true);

                log.info(
                        "Training reminder sent for booking {}",
                        booking.getId()
                );
            }
        }
    }

    private String buildReminderMessage(Booking booking) {

        TrainingSession session = booking.getSession();

        return """
                🥊 Training reminder

                Your training starts in about one hour.

                %s
                📅 %s
                👤 %s
                """.formatted(
                session.getTitle(),
                session.getStartTime().format(FORMATTER),
                session.getTrainer().getLastName()
        );
    }
}
