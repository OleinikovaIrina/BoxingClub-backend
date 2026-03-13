package de.oleinikova.boxingclub.backend.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiRequestDto(

        @NotBlank(message = "{aiMessage.question.notBlank}")
        @Schema(
                description = "User question about boxing training",
                example = "How can I improve boxing stamina?"
        )
        String question
) {

}

