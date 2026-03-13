package de.oleinikova.boxingclub.backend.ai.service.impl;

import de.oleinikova.boxingclub.backend.ai.dto.request.AiRequestDto;
import de.oleinikova.boxingclub.backend.ai.dto.response.AiResponseDto;
import de.oleinikova.boxingclub.backend.ai.service.interfaces.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    @Value("${ai.groq.key}")
    private String groqApiKey;

    @Value("${ai.groq.url}")
    private String groqUrl;

    private final WebClient webClient;

    @Override
    public AiResponseDto askQuestion(AiRequestDto requestDto) {

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system",
                                "content", "You are a professional boxing trainer. Answer only boxing-related questions. Answer in the same language as the user."
                        ),
                        Map.of("role", "user",
                                "content", requestDto.question()
                        )
                )
        );

        Map<String, Object> response =
                webClient.post()
                        .uri(groqUrl)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + groqApiKey)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();


        if (response == null) {
            return new AiResponseDto("AI service returned empty response");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        String answer = (String) message.get("content");

        return new AiResponseDto(answer);
    }
}
