package de.oleinikova.boxingclub.backend.security.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails user;

    @BeforeEach
    void setup() {
        String secretBase64 = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        jwtService = new JwtService(secretBase64, 1);

        user = User.withUsername("test@mail.com")
                .password("123")
                .roles("ADMIN")
                .build();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {


        String token = jwtService.generateToken(user);
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@mail.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("ROLE_ADMIN");
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@mail.com");
        assertThat(jwtService.isValid(token,user)).isTrue();
    }
}