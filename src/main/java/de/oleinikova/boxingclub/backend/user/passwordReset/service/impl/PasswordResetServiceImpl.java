package de.oleinikova.boxingclub.backend.user.passwordReset.service.impl;

import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetStatus;
import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetToken;
import de.oleinikova.boxingclub.backend.user.passwordReset.persistence.PasswordResetRepository;
import de.oleinikova.boxingclub.backend.user.passwordReset.service.interfaces.PasswordResetService;
import de.oleinikova.boxingclub.backend.exception.InvalidTokenException;
import de.oleinikova.boxingclub.backend.exception.TokenExpiredException;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;

import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRepository tokenRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${confirmation.token.expiration-hours:1}")
    private int expirationHours;

    @Override
    @Transactional
    public PasswordResetToken createPasswordResetToken(String email) {
        AppUser user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new UserNotFoundException());
        tokenRepository.deleteByUser(user);
        PasswordResetToken token = PasswordResetToken.createForUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));

        return tokenRepository.save(token);
    }

    private PasswordResetToken validateAndGetActiveToken(String tokenValue) {
        PasswordResetToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Password reset token expired");
        }
        if (token.getStatus() != PasswordResetStatus.PENDING) {
            throw new InvalidTokenException("Token  already used");
        }
        return token;

    }

    @Override
    @Transactional(readOnly = true)
    public boolean validatePasswordResetToken(String tokenValue) {
        validateAndGetActiveToken(tokenValue);
        return true;
    }

    @Override
    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken token = validateAndGetActiveToken(tokenValue);
        AppUser user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        token.setStatus(PasswordResetStatus.CONFIRMED);
        tokenRepository.save(token);
    }

}

