package de.oleinikova.boxingclub.backend.user.passwordReset.service.impl;

import de.oleinikova.boxingclub.backend.mail.EmailService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRepository tokenRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${security.password-reset.expiration-seconds}")
    private long expirationSeconds;

    @Override
    @Transactional
    public void createPasswordResetToken(String email) {
        userRepository.findByEmailIgnoreCase(email)
                .ifPresent(user -> {
                    tokenRepository.deleteByUser(user);

                    PasswordResetToken token = PasswordResetToken.createForUser(user);
                    token.setExpiresAt(Instant.now().plusSeconds(expirationSeconds ));

                    tokenRepository.save(token);
                    sendPasswordResetEmail(user, token.getToken());
                    log.warn("RESET TOKEN CREATED at {}", Instant.now());

                });
    }

    private PasswordResetToken validateAndGetActiveToken(String tokenValue) {
        PasswordResetToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new TokenExpiredException("Password reset token expired");
        }
        if (token.getStatus() != PasswordResetStatus.PENDING) {
            throw new InvalidTokenException("Token  already used");
        }
        log.warn("VALIDATE expiresAt={}, now={}", token.getExpiresAt(), Instant.now());
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

    private void sendPasswordResetEmail(AppUser user, String token) {
        String link = "http://localhost:5173/#/reset-password?token=" + token;


        String html =
                "<p>Hello " + user.getFirstName() + ",</p>"
                        + "<p>We received a request to reset your password.</p>"
                        + "<p>Click the link below to set a new password:</p>"
                        + "<p><a href=\"" + link + "\">RESET PASSWORD</a></p>"
                        + "<p>If you did not request this, you can ignore this email.</p>";
        try {
            emailService.sendHtmlMail(
                    user.getEmail(),
                    "BoxingClub – Reset your password",
                    html
            );
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            ;
        }
    }
}


