package de.oleinikova.boxingclub.backend.user.exception;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends RestApiException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "User not found");
    }
}
