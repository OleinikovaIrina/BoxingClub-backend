package de.oleinikova.boxingclub.backend.security.api;

public record LoginResponse(String accessToken, String role) {
}