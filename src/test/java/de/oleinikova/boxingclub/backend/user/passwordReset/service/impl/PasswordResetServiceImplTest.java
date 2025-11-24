package de.oleinikova.boxingclub.backend.user.passwordReset.service.impl;

import de.oleinikova.boxingclub.backend.exception.InvalidTokenException;
import de.oleinikova.boxingclub.backend.exception.TokenExpiredException;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetStatus;
import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetToken;
import de.oleinikova.boxingclub.backend.user.passwordReset.persistence.PasswordResetRepository;
import de.oleinikova.boxingclub.backend.user.passwordReset.service.interfaces.PasswordResetService;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private PasswordResetRepository tokenRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    @Test
    void shouldCreatePasswordResetTokenSuccessfully() {
        String email = "test@mail.com";
        AppUser user = new AppUser();
        user.setEmail(email);
        when(userRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(user));
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenReturn(token);

        PasswordResetToken result = passwordResetService.createPasswordResetToken(email);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));
        verify(userRepository).findByEmailIgnoreCase(email);
        verify(tokenRepository).deleteByUser(user);
        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        String email = "test@mail.com";
        AppUser user = new AppUser();
        user.setEmail(email);
        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> passwordResetService.createPasswordResetToken(email));

        verify(tokenRepository, never()).save(any());
        verify(tokenRepository, never()).deleteByUser(any());
    }

    @Test
    void shouldValidatePasswordResetTokenSuccessfully() {
        String tokenValue = "de6c7002-3ee1-4ad0-b802-dc85c0ed511f";
        AppUser user = new AppUser();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setStatus(PasswordResetStatus.PENDING);
        when(tokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        boolean result = passwordResetService.validatePasswordResetToken(tokenValue);

        assertTrue(result);
        verify(tokenRepository).findByToken(tokenValue);
    }

    @Test
    void shouldThrowException_WhenTokenNotFound() {
        String tokenValue = "de6c7002-3ee1-4ad0-b802-dc85c0ed511f";

        when(tokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> passwordResetService.validatePasswordResetToken(tokenValue));
        verify(tokenRepository).findByToken(tokenValue);
    }

    @Test
    void shouldThrowException_WhenTokenExpired() {
        String tokenValue = "de6c7002-3ee1-4ad0-b802-dc85c0ed511f";
        AppUser user = new AppUser();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().minusHours(1));
        token.setStatus(PasswordResetStatus.PENDING);

        when(tokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        assertThrows(TokenExpiredException.class, () -> passwordResetService.validatePasswordResetToken(tokenValue));
        verify(tokenRepository).findByToken(tokenValue);
    }

    @Test
    void shouldThrowException_WhenTokenAlreadyUsed() {
        String tokenValue = "de6c7002-3ee1-4ad0-b802-dc85c0ed511f";
        AppUser user = new AppUser();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setStatus(PasswordResetStatus.CONFIRMED);

        when(tokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> passwordResetService.validatePasswordResetToken(tokenValue));
        verify(tokenRepository).findByToken(tokenValue);
    }

    @Test
    void shouldResetPasswordResetTokenSuccessfully() {
        String tokenValue = "de6c7002-3ee1-4ad0-b802-dc85c0ed511f";
        String newPassword = "Box_club2025!";
        AppUser user = new AppUser();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setStatus(PasswordResetStatus.PENDING);

        when(tokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode(newPassword))
                .thenReturn("encoded");

        passwordResetService.resetPassword(tokenValue, newPassword);

        assertEquals("encoded", user.getPassword());
        assertEquals(PasswordResetStatus.CONFIRMED, token.getStatus());

        verify(tokenRepository).findByToken(tokenValue);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);

    }
}