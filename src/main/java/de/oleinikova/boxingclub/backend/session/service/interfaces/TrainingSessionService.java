package de.oleinikova.boxingclub.backend.session.service.interfaces;

import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionUpdateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.TrainingSessionResponseDto;

import java.util.List;
import java.util.UUID;

public interface TrainingSessionService {

    TrainingSessionResponseDto createTrainingSession(TrainingSessionCreateRequestDto dto);

    TrainingSessionResponseDto getTrainingSessionById(UUID sessionId);

    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerId(UUID trainerId);

    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastName(String lastName);

    List<TrainingSessionResponseDto> getTrainingSessionsByTitle(String title);

    TrainingSessionResponseDto cancelTrainingSession(UUID sessionId);

    List<TrainingSessionResponseDto> getAllActiveSessions();

    List<TrainingSessionResponseDto> getTrainingSessionsByTitleAndStartTimeAfter(String title);

    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerIdAndStartTimeAfter(UUID trainerId);

    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastNameAndStartTimeAfter(String lastName);

    List<TrainingSessionResponseDto> getAllActiveSessionsAndStartTimeAfter();

    TrainingSessionResponseDto updateTrainingSession(UUID sessionId, TrainingSessionUpdateRequestDto dto);


}
