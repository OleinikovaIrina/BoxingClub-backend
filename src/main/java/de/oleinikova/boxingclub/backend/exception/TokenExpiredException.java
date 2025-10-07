package de.oleinikova.boxingclub.backend.exception;

/** 410: доменный токен (reset/confirm) просрочен или уже использован. */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) { super(message); }
}
