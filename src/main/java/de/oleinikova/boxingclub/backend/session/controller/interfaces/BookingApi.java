package de.oleinikova.boxingclub.backend.session.controller.interfaces;

import de.oleinikova.boxingclub.backend.session.dto.request.BookingCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.BookingResponseDto;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/user/bookings")
public interface BookingApi extends BookingApiSwaggerDoc {

    @PostMapping
    BookingResponseDto createBooking(@RequestBody @Valid BookingCreateRequestDto dto, Authentication authentication);

    @PostMapping("/{bookingId}/cancel")
    void cancelBooking(@PathVariable UUID bookingId, Authentication authentication);

    @GetMapping
    List<BookingResponseDto> getAllMyBookings(Authentication authentication);
}
