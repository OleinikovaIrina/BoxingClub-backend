package de.oleinikova.boxingclub.backend.session.controller.interfaces;

import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionCreateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.request.TrainingSessionUpdateRequestDto;
import de.oleinikova.boxingclub.backend.session.dto.response.TrainingSessionResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/training")
public interface TrainingApi extends TrainingApiSwaggerDoc {


    @PostMapping
    TrainingSessionResponseDto createTrainingSession(@RequestBody @Valid TrainingSessionCreateRequestDto dto);

    @GetMapping("/{sessionId}")
    TrainingSessionResponseDto getTrainingSessionById(@PathVariable UUID sessionId);

    @GetMapping("/title")
    List<TrainingSessionResponseDto> getTrainingSessionsByTitle(@RequestParam String title);

    @GetMapping("/trainer/{trainerId}")
    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerId(@PathVariable UUID trainerId);

    @GetMapping("/trainer")
    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastName(@RequestParam String trainerLastName);

    @PostMapping("/{sessionId}/cancel")
    TrainingSessionResponseDto cancelTrainingSession(@PathVariable UUID sessionId);

    @GetMapping("/active")
    List<TrainingSessionResponseDto> getAllActiveSessions();

    @GetMapping("/title/future")
    List<TrainingSessionResponseDto> getTrainingSessionsByTitleAndStartTimeAfter(@RequestParam String title);

    @GetMapping("/trainer/{trainerId}/future")
    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerIdAndStartTimeAfter(@PathVariable UUID trainerId);

    @GetMapping("/trainer/future")
    List<TrainingSessionResponseDto> getTrainingSessionsByTrainerLastNameAndStartTimeAfter(@RequestParam String trainerLastName);

    @GetMapping("/active/future")
    List<TrainingSessionResponseDto> getAllActiveSessionsAndStartTimeAfter();

    @PutMapping("/{sessionId}")
    TrainingSessionResponseDto updateTrainingSession(@PathVariable UUID sessionId, @RequestBody @Valid TrainingSessionUpdateRequestDto dto);
}