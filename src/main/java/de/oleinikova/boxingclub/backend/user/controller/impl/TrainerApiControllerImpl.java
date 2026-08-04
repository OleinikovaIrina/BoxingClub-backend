package de.oleinikova.boxingclub.backend.user.controller.impl;

import de.oleinikova.boxingclub.backend.user.controller.interfaces.TrainerApi;
import de.oleinikova.boxingclub.backend.user.dto.request.TrainerCreateRequestDto;
import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;
import de.oleinikova.boxingclub.backend.user.service.interfaces.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TrainerApiControllerImpl implements TrainerApi {

    private final TrainerService trainerService;

    @Override
    public TrainerResponseDto createTrainer(TrainerCreateRequestDto dto) {
        return trainerService.createTrainer(dto);
    }

    @Override
    public void deactivateTrainer(UUID trainerId) {
        trainerService.deactivateTrainer(trainerId);
    }
}
