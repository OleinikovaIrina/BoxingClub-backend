package de.oleinikova.boxingclub.backend.ai.controller.impl;

import de.oleinikova.boxingclub.backend.ai.controller.interfaces.AiApi;
import de.oleinikova.boxingclub.backend.ai.dto.request.AiRequestDto;
import de.oleinikova.boxingclub.backend.ai.dto.response.AiResponseDto;
import de.oleinikova.boxingclub.backend.ai.service.interfaces.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiControllerImpl implements AiApi {

    private  final AiService aiService;

    @Override
    public AiResponseDto askQuestion(AiRequestDto requestDto) {
        return aiService.askQuestion(requestDto);
    }
}
