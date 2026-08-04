package de.oleinikova.boxingclub.backend.telegram.link.service.impl;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import de.oleinikova.boxingclub.backend.telegram.config.TelegramBotProperties;
import de.oleinikova.boxingclub.backend.telegram.link.dto.response.TelegramLinkResponseDto;
import de.oleinikova.boxingclub.backend.telegram.link.entity.TelegramLinkToken;
import de.oleinikova.boxingclub.backend.telegram.link.entity.TelegramLinkTokenStatus;
import de.oleinikova.boxingclub.backend.telegram.link.persistence.TelegramLinkTokenRepository;
import de.oleinikova.boxingclub.backend.telegram.link.service.interfaces.TelegramLinkService;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "telegram.bot.enabled",
        havingValue = "true"
)
public class TelegramLinkServiceImpl implements TelegramLinkService {

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private static final int TOKEN_BYTES = 32;

    private final TelegramLinkTokenRepository telegramLinkTokenRepository;
    private final AppUserRepository appUserRepository;
    private final TelegramBotProperties telegramBotProperties;

    @Override
    @Transactional
    public TelegramLinkResponseDto createLink(String email) {

        AppUser user = appUserRepository.findWithLockByEmailIgnoreCase(email)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isEnabled()) {
            throw new RestApiException(
                    HttpStatus.FORBIDDEN,
                    "User account is disabled."
            );
        }

        if (user.getConfirmationStatus()
                != ConfirmationStatus.CONFIRMED) {
            throw new RestApiException(
                    HttpStatus.FORBIDDEN,
                    "Email must be confirmed before linking Telegram."
            );
        }

        if (user.getTelegramChatId() != null) {
            throw new RestApiException(
                    HttpStatus.CONFLICT,
                    "Telegram is already linked to this account."
            );
        }


        cancelPendingTokens(user);


        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        LocalDateTime now = LocalDateTime.now();

        long expirationMinutes = telegramBotProperties.linkTokenExpirationMinutes();

        if (expirationMinutes <= 0) {
            throw new IllegalStateException(
                    "Telegram link token expiration must be greater than zero."
            );
        }
        LocalDateTime expiresAt = now.plusMinutes(expirationMinutes);

        TelegramLinkToken linkToken = new TelegramLinkToken();

        linkToken.setUser(user);
        linkToken.setTokenHash(tokenHash);
        linkToken.setStatus(TelegramLinkTokenStatus.PENDING);
        linkToken.setCreatedAt(now);
        linkToken.setExpiresAt(expiresAt);

        telegramLinkTokenRepository.save(linkToken);

        String telegramUrl = buildTelegramUrl(rawToken);

        return new TelegramLinkResponseDto(telegramUrl, expiresAt);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String completeLink(String rawToken, Long telegramChatId) {

        if (rawToken == null
                || rawToken.isBlank()
                || telegramChatId == null) {

            return """
                    Invalid Telegram link.
                    Please create a new link in your BoxingClub account.
                    """;
        }

        String tokenHash = hashToken(rawToken);

        Optional<TelegramLinkToken> optionalLinkToken = telegramLinkTokenRepository.findByTokenHashAndStatus(tokenHash, TelegramLinkTokenStatus.PENDING);

        if (optionalLinkToken.isEmpty()) {
            return """
                    This Telegram link is invalid or has already been used.
                    Please create a new link in your BoxingClub account.
                    """;
        }

        TelegramLinkToken linkToken = optionalLinkToken.get();

        LocalDateTime now = LocalDateTime.now();
        if (!linkToken.getExpiresAt().isAfter(now)) {

            linkToken.setStatus(TelegramLinkTokenStatus.CANCELLED);

            telegramLinkTokenRepository.save(linkToken);
            return """
                    This Telegram link has expired.
                    Please create a new link in your BoxingClub account.
                    """;
        }

        AppUser user = linkToken.getUser();

        if (!user.isEnabled()) {

            cancelToken(linkToken);

            return """
                    This BoxingClub account is disabled.
                    Telegram cannot be linked.
                    """;
        }

        if (user.getConfirmationStatus()
                != ConfirmationStatus.CONFIRMED) {

            cancelToken(linkToken);

            return """
                    Email confirmation is required.
                    Telegram cannot be linked.
                    """;
        }

        Optional<AppUser> userLinkedToChat = appUserRepository.findByTelegramChatId(telegramChatId);

        if (userLinkedToChat.isPresent() && !userLinkedToChat.get().getId().equals(user.getId())) {

            cancelToken(linkToken);

            return """
                    This Telegram account is already linked
                    to another BoxingClub account.
                    """;
        }

        Long currentTelegramChatId = user.getTelegramChatId();

        if (currentTelegramChatId != null
                && !currentTelegramChatId.equals(
                telegramChatId
        )) {

            cancelToken(linkToken);

            return """
                    This BoxingClub account is already linked
                    to another Telegram account.
                    """;
        }
        user.setTelegramChatId(telegramChatId);
        linkToken.setStatus(TelegramLinkTokenStatus.USED);
        linkToken.setUsedAt(now);

        appUserRepository.save(user);
        telegramLinkTokenRepository.save(linkToken);

        return """
                ✅ Telegram successfully linked
                to your BoxingClub account.
                """;
    }

    private void cancelPendingTokens(AppUser user) {

        List<TelegramLinkToken> pendingTokens = telegramLinkTokenRepository
                .findAllByUserAndStatus(user, TelegramLinkTokenStatus.PENDING);

        pendingTokens.forEach(token -> token.setStatus(TelegramLinkTokenStatus.CANCELLED));

        telegramLinkTokenRepository.saveAll(pendingTokens);

    }

    private void cancelToken(TelegramLinkToken linkToken) {

        linkToken.setStatus(TelegramLinkTokenStatus.CANCELLED);

        telegramLinkTokenRepository.save(linkToken);
    }

    private String generateToken() {

        byte[] randomBytes = new byte[TOKEN_BYTES];

        SECURE_RANDOM.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }


    private String hashToken(String rawToken) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawToken.getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    e
            );
        }
    }

    private String buildTelegramUrl(String rawToken) {

        String username = telegramBotProperties.username();


        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                    "Telegram bot username is not configured."
            );
        }
        if (username.startsWith("@")) {
            username = username.substring(1);
        }
        return "https://t.me/"
                + username
                + "?start="
                + rawToken;
    }
}