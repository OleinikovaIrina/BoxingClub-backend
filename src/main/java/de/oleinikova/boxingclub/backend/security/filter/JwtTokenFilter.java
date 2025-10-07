package de.oleinikova.boxingclub.backend.security.filter;

import de.oleinikova.boxingclub.backend.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String uri = request.getRequestURI();

        // 0) Разрешаем preflight-запросы CORS
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 1) Полностью пропускаем технические/публичные эндпоинты
        if (isPublicPath(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) Стандартная обработка JWT
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String username = null;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception ignored) {
            // Невалидный токен — продолжаем без аутентификации
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtService.isValid(token, user)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String uri) {
        // H2 console
        if (uri.startsWith("/h2-console")) return true;
        // Actuator health
        if ("/actuator/health".equals(uri)) return true;
        // Swagger/OpenAPI
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) return true;
        // Логин
        if (uri.equals("/api/auth/login")) return true;
        if (uri.equals("/api/users/register")) return true;
        if (uri.equals("/api/users/confirm")) return true;
        return false;
    }
}
