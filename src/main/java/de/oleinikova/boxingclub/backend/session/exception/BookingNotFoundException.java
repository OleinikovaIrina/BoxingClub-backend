package de.oleinikova.boxingclub.backend.session.exception;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends RestApiException {
    public BookingNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Booking not found");
    }
}
