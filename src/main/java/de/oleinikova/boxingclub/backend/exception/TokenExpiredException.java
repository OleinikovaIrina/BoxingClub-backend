package de.oleinikova.boxingclub.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 410: доменный токен (reset/confirm) просрочен или уже использован.
 */
@ResponseStatus(HttpStatus.GONE)
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
