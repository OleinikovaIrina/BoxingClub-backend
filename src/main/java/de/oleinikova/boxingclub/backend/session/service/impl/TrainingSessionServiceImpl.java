package de.oleinikova.boxingclub.backend.session.service.impl;

import de.oleinikova.boxingclub.backend.exception.RestApiException;
import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionUpdateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.TrainingSessionResponseDto;
import de.oleinikova.boxingclub.backend.session.entity.SessionType;
import de.oleinikova.boxingclub.backend.session.entity.TrainingSession;
import de.oleinikova.boxingclub.backend.session.exception.SessionNotFoundException;
import de.oleinikova.boxingclub.backend.session.persistence.BookingRepository;
import de.oleinikova.boxingclub.backend.session.persistence.TrainingSessionRepository;
import de.oleinikova.boxingclub.backend.session.service.interfaces.TrainingSessionService;
import de.oleinikova.boxingclub.backend.session.util.TrainingSessionMapper;
import de.oleinikova.boxingclub.backend.user.entity.AppUser;
import de.oleinikova.boxingclub.backend.user.entity.Role;
import de.oleinikova.boxingclub.backend.user.exception.UserNotFoundException;
import de.oleinikova.boxingclub.backend.user.persistence.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainingSessionMapper mapper;
    private final AppUserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public TrainingSessionResponseDto createTrainingSession(TrainingSessionCreateRequestDto dto) {

        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Training session cannot be in the past");
        }

        if (dto.type() == SessionType.INDIVIDUAL && dto.maxParticipants() != 1) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Individual session must have exactly 1 participant");
        }

        TrainingSession session = mapper.toEntity(dto);

        AppUser trainer = userRepository.findById(dto.trainerId()).orElseThrow(UserNotFoundException::new);

        if (trainer.getRole() != Role.ROLE_TRAINER || !trainer.isEnabled()) {
            throw new RestApiException(HttpStatus.BAD_REQUEST,  "Trainer is not available");
        }

        session.setTrainer(trainer);
        session.setCancelled(false);

        List<TrainingSession> sessions = trainingSessionRepository.findAllByTrainer_IdAndCancelledFalse(dto.trainerId());

        LocalDateTime newStart = dto.dateTime();
        LocalDateTime newEnd = newStart.plusMinutes(dto.durationMinutes());

        boolean over = sessions.stream()
                .anyMatch(s -> newStart.isBefore(s.getEndTime()) && newEnd.isAfter(s.getStartTime()));

        if (over) {
            throw new RestApiException(HttpStatus.BAD_REQUEST,
                    "Trainer already has a session at this time");
        }

        TrainingSession saved = trainingSessionRepository.save(session);


        return mapper.toDto(saved, saved.getMaxParticipants());
    }


    @Override
    public TrainingSessionResponseDto getTrainingSessionById(UUID sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId).
                orElseThrow(SessionNotFoundException::new);
        int availableSlots = calculateAvailableSlots(session);
        return mapper.toDto(session, availableSlots);
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTitle(String title) {
        return trainingSessionRepository.findAllByTitleIgnoreCaseAndCancelledFalse(title)
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTitleAndStartTimeAfter(String title) {

        return trainingSessionRepository.findAllByTitleIgnoreCaseAndCancelledFalseAndStartTimeAfter(title, LocalDateTime.now())
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerId(UUID trainerId) {

        return trainingSessionRepository.findAllByTrainer_IdAndCancelledFalse(trainerId)
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerIdAndStartTimeAfter(UUID trainerId) {

        return trainingSessionRepository.findAllByTrainer_IdAndCancelledFalseAndStartTimeAfter(trainerId, LocalDateTime.now())
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }


    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastName(String lastName) {

        return trainingSessionRepository.findAllByTrainer_LastNameIgnoreCaseAndCancelledFalse(lastName)
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastNameAndStartTimeAfter(String lastName) {

        return trainingSessionRepository.findAllByTrainer_LastNameIgnoreCaseAndCancelledFalseAndStartTimeAfter(lastName, LocalDateTime.now())
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }

    @Transactional
    @Override
    public TrainingSessionResponseDto cancelTrainingSession(UUID sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId).
                orElseThrow(SessionNotFoundException::new);

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Past trainings cannot be cancelled");
        }

        if (session.isCancelled()) {
            return mapper.toDto(session, 0);
        }

        session.setCancelled(true);

        TrainingSession saved = trainingSessionRepository.save(session);
        return mapper.toDto(saved, 0);
    }

    @Override
    public List<TrainingSessionResponseDto> getAllActiveSessions() {

        return trainingSessionRepository.findAllByCancelledFalse()
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }

    @Override
    public List<TrainingSessionResponseDto> getAllActiveSessionsAndStartTimeAfter() {

        return trainingSessionRepository.findAllByCancelledFalseAndStartTimeAfter(LocalDateTime.now())
                .stream()
                .map(session -> {
                    int availableSlots = calculateAvailableSlots(session);
                    return mapper.toDto(session, availableSlots);
                })
                .toList();
    }

    @Transactional
    @Override
    public TrainingSessionResponseDto updateTrainingSession(UUID sessionId, TrainingSessionUpdateRequestDto dto) {

        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Training session cannot be in the past");
        }

        AppUser trainer = userRepository.findById(dto.trainerId()).orElseThrow(UserNotFoundException::new);

        if (trainer.getRole() != Role.ROLE_TRAINER) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "User is not a trainer");
        }

        TrainingSession session = trainingSessionRepository.findByIdAndCancelledFalse(sessionId).orElseThrow(SessionNotFoundException::new);

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Past trainings cannot be updated");
        }
        int activeBookings =
                (int) bookingRepository
                        .countBySession_IdAndCancelledFalse(session.getId());

        if (activeBookings > dto.maxParticipants()) {
            throw new RestApiException(HttpStatus.BAD_REQUEST, "Max participants cannot be less than active bookings");

        }

        List<TrainingSession> sessions = trainingSessionRepository.findAllByTrainer_IdAndCancelledFalse(dto.trainerId());

        LocalDateTime newStart = dto.dateTime();
        LocalDateTime newEnd = newStart.plusMinutes(dto.durationMinutes());

        boolean over = sessions.stream()
                .filter(s -> !s.getId().equals(sessionId))
                .anyMatch(s -> newStart.isBefore(s.getEndTime()) && newEnd.isAfter(s.getStartTime()));

        if (over) {
            throw new RestApiException(HttpStatus.BAD_REQUEST,
                    "Trainer already has a session at this time");
        }
        session.setTitle(dto.title());
        session.setStartTime(dto.dateTime());
        session.setDurationMinutes(dto.durationMinutes());
        session.setMaxParticipants(dto.maxParticipants());
        session.setTrainer(trainer);


        TrainingSession saved = trainingSessionRepository.save(session);

        return mapper.toDto(saved, calculateAvailableSlots(saved));


    }

    private int calculateAvailableSlots(TrainingSession session) {

        int activeBookings =
                (int) bookingRepository
                        .countBySession_IdAndCancelledFalse(session.getId());

        return session.getMaxParticipants() - activeBookings;
    }
}

