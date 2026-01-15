package de.oleinikova.boxingclub.backend.user.service.impl;

import de.oleinikova.boxingclub.backend.mail.EmailService;
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
    private final EmailService emailService;

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
                String token = confirmationService.regenerateCode(ex);
                sendConfirmationEmail(ex,token);

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

        String token = confirmationService.generateConfirmationCode(saved);
        sendConfirmationEmail(saved, token);

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

    private void sendConfirmationEmail(AppUser user, String token) {
        String link = "http://localhost:8081/api/auth/confirm?code=" + token;


        String html = "<p>Hello " + user.getFirstName() + ",</p>"
                + "<p>Thank you for registering in <b>BoxingClub</b>!</p>"
                + "<p>Please confirm your email by clicking the link below:</p>"
                + "<p><a href=\"" + link + "\">CONFIRM ACCOUNT</a></p>"
                + "<p>The link is valid for 24 hours.</p>";

        try {
            emailService.sendHtmlMail(
                    user.getEmail(),
                    "Boxing Club - Confirm your account",

                    html
            );
            log.info("Confirmation email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email", e);
        }
    }
}

