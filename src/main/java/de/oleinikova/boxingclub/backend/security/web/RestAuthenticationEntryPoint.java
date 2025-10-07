package de.oleinikova.boxingclub.backend.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.oleinikova.boxingclub.backend.common.error.response.ErrorResponseDto;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component @RequiredArgsConstructor @Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper om;
    @Override public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
        var body = new ErrorResponseDto(LocalDateTime.now(), 401, "Unauthorized", "Authentication required", List.of(), req.getRequestURI());
        res.setStatus(401); res.setContentType("application/json"); om.writeValue(res.getWriter(), body);
        log.warn("401 Unauthorized: {}", ex.getMessage());
    }
}