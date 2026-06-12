package de.oleinikova.boxingclub.backend.session.controller.interfaces;


import de.oleinikova.boxingclub.backend.session.dto.request.BookingCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.BookingResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Booking", description = "Booking for authenticated users")
public interface BookingApiSwaggerDoc {

    default BookingResponseDto createBooking(BookingCreateRequestDto dto) {
        throw new UnsupportedOperationException();
    }

    default void cancelBooking(UUID bookingId) {
        throw new UnsupportedOperationException();
    }

    default List<BookingResponseDto> getAllMyBookings() {
        throw new UnsupportedOperationException();
    }
}
