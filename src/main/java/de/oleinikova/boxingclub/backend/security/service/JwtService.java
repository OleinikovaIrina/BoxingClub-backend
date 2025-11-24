package de.oleinikova.boxingclub.backend.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/** Provides JWT generation, parsing and validation (HS256, Base64 secret). */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpMillis;

    public JwtService(
            @Value("${security.jwt.secret}") String secretBase64,
            @Value("${security.jwt.access-exp-min}") long expMin
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.accessExpMillis = expMin * 60_000L;
    }

    /** Создание токена: subject=email, claim=role */
    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date(now.toEpochMilli() + accessExpMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Извлекаем email */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Извлекаем роль (для отладки или тестов) */
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    /** Проверка токена */
    public boolean isValid(String token, UserDetails user) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            String subject = jws.getBody().getSubject();
            Date exp = jws.getBody().getExpiration();
            return subject != null
                    && subject.equals(user.getUsername())
                    && exp != null
                    && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}