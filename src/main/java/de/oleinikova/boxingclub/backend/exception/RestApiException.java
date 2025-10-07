package de.oleinikova.boxingclub.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class RestApiException extends RuntimeException {

    @Getter
    private final HttpStatus httpStatus;

    public RestApiException(HttpStatus httpStatus, String message) {
        super(message);

        this.httpStatus = httpStatus;
    }
}
