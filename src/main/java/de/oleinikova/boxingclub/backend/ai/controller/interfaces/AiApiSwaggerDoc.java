package de.oleinikova.boxingclub.backend.ai.controller.interfaces;

import de.oleinikova.boxingclub.backend.ai.dto.request.AiRequestDto;
import de.oleinikova.boxingclub.backend.ai.dto.response.AiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AI Assistant", description = "AI based boxing training advisor")
public interface AiApiSwaggerDoc {

    @Operation(summary = "Ask AI  for boxing training advice")
    AiResponseDto askQuestion(AiRequestDto requestDto);
}
