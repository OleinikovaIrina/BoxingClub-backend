package de.oleinikova.boxingclub.backend.session.controller.interfaces;

import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionUpdateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.TrainingSessionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Training Sessions", description = "Operations for managing training sessions")
public interface TrainingApiSwaggerDoc {

    @Operation(summary = "Create a new training session")
    default TrainingSessionResponseDto createTrainingSession(TrainingSessionCreateRequestDto dto) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get training session by ID")
    default TrainingSessionResponseDto getTrainingSessionById(UUID sessionId) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get training session by title")
    default List<TrainingSessionResponseDto> getTrainingSessionsByTitle(@RequestParam String title) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get all training sessions by trainer ID")
    default List<TrainingSessionResponseDto> getTrainingSessionsByTrainerId(UUID trainerId) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get all training sessions by trainer Last name")
    default List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastName(@RequestParam String trainerLastName) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Cancel (soft delete) a training session")
    default TrainingSessionResponseDto cancelTrainingSession(UUID sessionId) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get all active (not cancelled) training sessions")
    default List<TrainingSessionResponseDto> getAllActiveSessions() {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get future training sessions by title")
    default List<TrainingSessionResponseDto>
    getTrainingSessionsByTitleAndStartTimeAfter(
            @RequestParam String title
    ) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get future training sessions by trainer ID")
    default List<TrainingSessionResponseDto>
    getTrainingSessionsByTrainerIdAndStartTimeAfter(
            UUID trainerId
    ) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get future training sessions by trainer last name")
    default List<TrainingSessionResponseDto>
    getTrainingSessionsByTrainerLastNameAndStartTimeAfter(
            @RequestParam String trainerLastName
    ) {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Get all future active training sessions")
    default List<TrainingSessionResponseDto>
    getAllActiveSessionsAndStartTimeAfter() {
        throw new UnsupportedOperationException();
    }

    @Operation(summary = "Update a training session")
    default TrainingSessionResponseDto updateTrainingSession(
            UUID sessionId,
            TrainingSessionUpdateRequestDto dto
    ) {
        throw new UnsupportedOperationException();
    }
}