package de.oleinikova.boxingclub.backend.user.service.impl;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import de.oleinikova.boxingclub.backend.user.dto.request.TrainerCreateRequestDto;
import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.ConfirmationStatus;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import de.oleinikova.boxingclub.backend.user.service.interfaces.TrainerService;
import de.oleinikova.boxingclub.backend.user.util.TrainerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final AppUserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final TrainerMapper trainerMapper;

    private String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    @Transactional
    public TrainerResponseDto createTrainer(TrainerCreateRequestDto dto) {

        String email = normalizeEmail(dto.email());

        if (repo.existsByEmailIgnoreCase(email)) {
            throw new RestApiException(HttpStatus.CONFLICT, "Email already used");
        }

        AppUser trainer = trainerMapper.toEntity(dto);
        trainer.setFirstName(dto.firstName().trim());
        trainer.setLastName(dto.lastName().trim());
        trainer.setEmail(email);
        trainer.setRole(Role.ROLE_TRAINER);
        trainer.setConfirmationStatus(ConfirmationStatus.CONFIRMED);
        trainer.setPassword(passwordEncoder.encode(dto.password()));

        AppUser saved = repo.save(trainer);
        return trainerMapper.toTrainerResponseDto(saved);
    }

    @Override
    @Transactional
    public void deactivateTrainer(UUID trainerId) {

        AppUser trainer = repo.findById(trainerId).
                orElseThrow(UserNotFoundException::new);

        if (trainer.getRole() != Role.ROLE_TRAINER) {
            throw new RestApiException(HttpStatus.CONFLICT, "The selected user is not a trainer.");
        }
        trainer.setEnabled(false);
        repo.save(trainer);
    }
}
