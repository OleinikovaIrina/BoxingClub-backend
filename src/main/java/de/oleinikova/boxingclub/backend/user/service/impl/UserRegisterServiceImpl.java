package de.oleinikova.boxingclub.backend.user.service.impl;

import de.oleinikova.boxingclub.backend.user.confirmation.interfaces.ConfirmationService;
import de.oleinikova.boxingclub.backend.user.dto.request.UserCreateDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserCreateResponseDto;
import de.oleinikova.boxingclub.backend.user.dto.response.UserResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import de.oleinikova.boxingclub.backend.user.service.interfaces.UserRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Locale;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisterServiceImpl implements UserRegisterService {
    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationService confirmationService;

    private String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    @Override
    public UserCreateResponseDto register(UserCreateDto userCreateDto) {

        final String email = normalizeEmail(userCreateDto.email());

        final Optional<AppUser> existing = userRepo.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            final AppUser ex = existing.get();
            if (ex.getConfirmationStatus() == ConfirmationStatus.UNCONFIRMED) {
                confirmationService.regenerateCode(ex);
                return new UserCreateResponseDto(
                        ex.getId(),
                        ex.getEmail(),
                        ex.getRole(),
                        true
                );
            }

        }
        final AppUser appUser = new AppUser();
        appUser.setFirstName(userCreateDto.firstName().trim());
        appUser.setLastName(userCreateDto.lastName().trim());
        appUser.setEmail(email);
        appUser.setPassword(passwordEncoder.encode(userCreateDto.password()));
        appUser.setRole(Role.ROLE_USER);
        appUser.setConfirmationStatus(ConfirmationStatus.UNCONFIRMED);
        appUser.setEnabled(true);
        AppUser saved = userRepo.save(appUser);

        confirmationService.generateConfirmationCode(saved);

        return new UserCreateResponseDto(
                saved.getId(),
                saved.getEmail(),
                saved.getRole(),
                false
        );
    }

    @Transactional
    @Override
    public UserResponseDto confirmRegistration(String code) {

        AppUser appUser = confirmationService.confirmAndConsume(code);
        appUser.setConfirmationStatus(ConfirmationStatus.CONFIRMED);
        AppUser updated = userRepo.save(appUser);
        return new UserResponseDto(
                updated.getEmail(),
                updated.getRole(),
                updated.getConfirmationStatus()
        );
    }
}

