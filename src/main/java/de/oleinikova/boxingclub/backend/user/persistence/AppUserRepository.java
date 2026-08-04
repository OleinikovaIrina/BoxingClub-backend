package de.oleinikova.boxingclub.backend.user.persistence;

import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {


    List<AppUser> findAllByLastNameIgnoreCase(String lastName);

    List<AppUser> findAllByRole(Role role);

    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<AppUser> findAllByRoleAndEnabledTrue(Role role);

    Optional<AppUser> findByTelegramChatId(Long telegramChatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AppUser> findWithLockByEmailIgnoreCase(String email);
}