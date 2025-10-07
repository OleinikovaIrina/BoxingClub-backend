package de.oleinikova.boxingclub.backend.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.oleinikova.boxingclub.backend.common.error.response.ErrorResponseDto;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component @RequiredArgsConstructor @Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper om;
    @Override public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        var body = new ErrorResponseDto(LocalDateTime.now(), 403, "Forbidden", "Access denied", List.of(), req.getRequestURI());
        res.setStatus(403); res.setContentType("application/json"); om.writeValue(res.getWriter(), body);
        log.warn("403 Forbidden: {}", ex.getMessage());
    }
}

