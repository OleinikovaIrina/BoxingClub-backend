package de.oleinikova.boxingclub.backend.session.controller.impl;

import de.oleinikova.boxingclub.backend.session.controller.interfaces.BookingApi;
import de.oleinikova.boxingclub.backend.session.controller.interfaces.BookingApiSwaggerDoc;
import de.oleinikova.boxingclub.backend.session.dto.request.BookingCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.BookingResponseDto;
import de.oleinikova.boxingclub.backend.session.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookingControllerImpl implements BookingApi {

    private final BookingService bookingService;

    @Override
    public BookingResponseDto createBooking(BookingCreateRequestDto dto, Authentication authentication) {
        String email = authentication.getName();

        return bookingService.createBooking(dto, email);
    }

    @Override
    public void cancelBooking(UUID bookingId, Authentication authentication) {
        String email = authentication.getName();

        bookingService.cancelBooking(bookingId, email);
    }

    @Override
    public List<BookingResponseDto> getAllMyBookings(Authentication authentication) {
        String email = authentication.getName();

        return bookingService.getAllMyBookings(email);
    }
}
