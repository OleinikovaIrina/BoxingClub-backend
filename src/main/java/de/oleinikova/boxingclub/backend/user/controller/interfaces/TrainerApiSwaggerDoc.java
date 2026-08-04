package de.oleinikova.boxingclub.backend.user.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.TrainerCreateRequestDto;
import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "Trainer Management", description = "Operations for managing trainers")
public interface TrainerApiSwaggerDoc {

    @Operation(summary = "Create Trainer")
    TrainerResponseDto createTrainer(TrainerCreateRequestDto dto);

    @Operation(summary = "Delete trainer by ID")
    void deactivateTrainer(UUID trainerId);
}
