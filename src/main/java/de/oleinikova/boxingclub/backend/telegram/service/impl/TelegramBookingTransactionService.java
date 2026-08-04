package de.oleinikova.boxingclub.backend.telegram.service.impl;

import de.oleinikova.boxingclub.backend.session.dto.request.BookingCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramBookingTransactionService {

    private final BookingService bookingService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createBooking(UUID sessionId, String email) {

        bookingService.createBooking(
                new BookingCreateRequestDto(sessionId),
                email);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelBooking(UUID bookingId, String email) {

        bookingService.cancelBooking(bookingId, email);
    }
}
