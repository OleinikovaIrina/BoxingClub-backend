package de.oleinikova.boxingclub.backend.user.persistence;

import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {


    List<AppUser> findAllByLastNameIgnoreCase(String lastName);

    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
