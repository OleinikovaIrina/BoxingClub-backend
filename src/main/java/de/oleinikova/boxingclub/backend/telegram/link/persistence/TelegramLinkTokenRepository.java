package de.oleinikova.boxingclub.backend.telegram.link.persistence;

import de.oleinikova.boxingclub.backend.telegram.link.entity.TelegramLinkToken;
import de.oleinikova.boxingclub.backend.telegram.link.entity.TelegramLinkTokenStatus;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TelegramLinkTokenRepository extends JpaRepository<TelegramLinkToken, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TelegramLinkToken> findByTokenHashAndStatus(String tokenHash, TelegramLinkTokenStatus status);

    List<TelegramLinkToken> findAllByUserAndStatus(AppUser user, TelegramLinkTokenStatus status);
}
