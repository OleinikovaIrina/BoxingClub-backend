package de.oleinikova.boxingclub.backend.session.service.interfaces;

import de.oleinikova.boxingclub.backend.session.dto.request.BookingCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.BookingResponseDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponseDto createBooking(BookingCreateRequestDto dto, String email);

    void cancelBooking(UUID bookingId, String email);

    List<BookingResponseDto> getAllMyBookings(String email);
}
