package de.oleinikova.boxingclub.backend.session.controller.impl;

import de.oleinikova.boxingclub.backend.session.controller.interfaces.TrainingApi;
import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionUpdateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.TrainingSessionResponseDto;
import de.oleinikova.boxingclub.backend.session.service.interfaces.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TrainingControllerImpl implements TrainingApi {

    private final TrainingSessionService trainingSessionService;

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TrainingSessionResponseDto createTrainingSession(TrainingSessionCreateRequestDto dto) {
        return trainingSessionService.createTrainingSession(dto);
    }

    @Override
    public TrainingSessionResponseDto getTrainingSessionById(UUID sessionId) {
        return trainingSessionService.getTrainingSessionById(sessionId);
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTitle(String title) {
        return trainingSessionService.getTrainingSessionsByTitle(title);
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerId(UUID trainerId) {
        return trainingSessionService.getTrainingSessionsByTrainerId(trainerId);
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastName(String trainerLastName) {
        return trainingSessionService.getTrainingSessionsByTrainerLastName(trainerLastName);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TrainingSessionResponseDto cancelTrainingSession(UUID sessionId) {
        return trainingSessionService.cancelTrainingSession(sessionId);
    }

    @Override
    public List<TrainingSessionResponseDto> getAllActiveSessions() {
        return trainingSessionService.getAllActiveSessions();
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTitleAndStartTimeAfter(String title) {
        return trainingSessionService.getTrainingSessionsByTitleAndStartTimeAfter(title);
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerIdAndStartTimeAfter(UUID trainerId) {
        return trainingSessionService.getTrainingSessionsByTrainerIdAndStartTimeAfter(trainerId);
    }

    @Override
    public List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastNameAndStartTimeAfter(String trainerLastName) {
        return trainingSessionService.getTrainingSessionsByTrainerLastNameAndStartTimeAfter(trainerLastName);
    }

    @Override
    public List<TrainingSessionResponseDto> getAllActiveSessionsAndStartTimeAfter() {
        return trainingSessionService.getAllActiveSessionsAndStartTimeAfter();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TrainingSessionResponseDto updateTrainingSession(UUID sessionId, TrainingSessionUpdateRequestDto dto) {
        return trainingSessionService.updateTrainingSession(sessionId, dto);
    }
}


