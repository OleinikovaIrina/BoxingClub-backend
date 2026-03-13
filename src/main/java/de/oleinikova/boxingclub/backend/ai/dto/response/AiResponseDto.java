package de.oleinikova.boxingclub.backend.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AiResponseDto(


        @Schema(
                description = "AI  generated response",
                example = "Focus on interval training and jump rope."
        )
        String answer
) {

}