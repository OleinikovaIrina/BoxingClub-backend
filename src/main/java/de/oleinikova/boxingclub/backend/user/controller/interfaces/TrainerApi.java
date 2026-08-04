package de.oleinikova.boxingclub.backend.user.controller.interfaces;

import de.oleinikova.boxingclub.backend.user.dto.request.TrainerCreateRequestDto;
import de.oleinikova.boxingclub.backend.user.dto.response.TrainerResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/admin/trainers")
public interface TrainerApi extends TrainerApiSwaggerDoc {

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    TrainerResponseDto createTrainer(@Valid @RequestBody TrainerCreateRequestDto dto);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{trainerId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateTrainer(@PathVariable UUID trainerId);
}
