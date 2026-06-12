package de.oleinikova.boxingclub.backend.session.exception;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import org.springframework.http.HttpStatus;

public class SessionNotFoundException extends RestApiException {
    public SessionNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Training session not found");
    }
}
