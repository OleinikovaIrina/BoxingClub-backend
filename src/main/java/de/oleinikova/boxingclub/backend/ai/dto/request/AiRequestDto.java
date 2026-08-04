package de.oleinikova.boxingclub.backend.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiRequestDto(

        @NotBlank(message = "{aiMessage.question.notBlank}")
        @Size(
                max = 500,
                message = "Question must not exceed 500 characters"
        )
        @Schema(
                description = "User question about boxing training",
                example = "How can I improve boxing stamina?"
        )
        String question
) {

}

