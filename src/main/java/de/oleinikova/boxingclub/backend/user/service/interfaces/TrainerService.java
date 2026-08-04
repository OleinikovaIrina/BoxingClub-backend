package de.oleinikova.boxingclub.backend.user.service.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.TrainerCreateRequestDto;
import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;

import java.util.List;
import java.util.UUID;

public interface TrainerService {

    TrainerResponseDto createTrainer(TrainerCreateRequestDto dto);

    void deactivateTrainer(UUID trainerId);
}
