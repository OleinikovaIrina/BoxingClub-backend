package de.oleinikova.boxingclub.backend.ai.controller.interfaces;

import de.oleinikova.boxingclub.backend.ai.dto.request.AiRequestDto;
import de.oleinikova.boxingclub.backend.ai.dto.response.AiResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/ai")
public interface AiApi extends AiApiSwaggerDoc{

    @Override
    @PostMapping("/training-advice")
    AiResponseDto askQuestion(@Valid @RequestBody AiRequestDto requestDto);
}
