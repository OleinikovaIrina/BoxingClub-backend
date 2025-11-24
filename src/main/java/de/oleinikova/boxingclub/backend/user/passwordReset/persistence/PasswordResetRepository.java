package de.oleinikova.boxingclub.backend.user.passwordReset.persistence;

import de.oleinikova.boxingclub.backend.user.passwordReset.entity.PasswordResetToken;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface  PasswordResetRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(AppUser user);
}
