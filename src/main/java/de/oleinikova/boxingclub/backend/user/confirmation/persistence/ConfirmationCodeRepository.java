package de.oleinikova.boxingclub.backend.user.confirmation.persistence;

import de.oleinikova.boxingclub.backend.user.confirmation.entity.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, UUID> {

    Optional<ConfirmationCode> findByCode (String code);

    void deleteByUser_Id(UUID userId);

    void deleteByExpiresAtBefore(LocalDateTime expiresAtBefore);
}
