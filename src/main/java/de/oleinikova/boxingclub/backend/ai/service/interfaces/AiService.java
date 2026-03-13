package de.oleinikova.boxingclub.backend.ai.service.interfaces;

import de.oleinikova.boxingclub.backend.ai.dto.request.AiRequestDto;
import de.oleinikova.boxingclub.backend.ai.dto.response.AiResponseDto;

public interface AiService {

    AiResponseDto askQuestion(AiRequestDto requestDto);

}
