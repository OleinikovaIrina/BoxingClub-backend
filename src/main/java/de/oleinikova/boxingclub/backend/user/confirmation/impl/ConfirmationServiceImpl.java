package de.oleinikova.boxingclub.backend.user.confirmation.impl;

import de.oleinikova.boxingclub.backend.user.confirmation.entity.ConfirmationCode;
import de.oleinikova.boxingclub.backend.user.confirmation.entity.ConfirmationTokenStatus;
import de.oleinikova.boxingclub.backend.user.confirmation.interfaces.ConfirmationService;
import de.oleinikova.boxingclub.backend.user.confirmation.persistence.ConfirmationCodeRepository;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationServiceImpl implements ConfirmationService {

    private final ConfirmationCodeRepository repo;

    @Value("${passwordReset.expiration.minutes:90}")
    private int ttlMinutes;
    @Transactional
    @Override
    public String generateConfirmationCode(AppUser appUser) {
        repo.deleteByUser_Id(appUser.getId());
        var token = new ConfirmationCode();
        token.setUser(appUser);
        token.setCode(UUID.randomUUID().toString());
        token.setStatus(ConfirmationTokenStatus.PENDING);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        repo.save(token);
        return token.getCode();
    }

    @Transactional
    @Override
    public String regenerateCode(AppUser appUser) {
        return generateConfirmationCode(appUser);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public AppUser confirmAndConsume(String code) {
        log.info("Confirm request received with code='{}' (class={})", code, code.getClass().getName());
        log.info("Code: {}", code);
        var token = repo.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid  code"));
        log.info("Token found: {}", token);
        log.info("User: {}", token.getUser());

        if (token.getStatus() != ConfirmationTokenStatus.PENDING)
            throw new IllegalStateException("Confirmation code already used");
        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Confirmation code expired");

        token.setStatus(ConfirmationTokenStatus.USED);
        repo.save(token);
        return token.getUser();
    }
}
