package de.oleinikova.boxingclub.backend.ai.service.impl;

import de.oleinikova.boxingclub.backend.ai.dto.request.AiRequestDto;
import de.oleinikova.boxingclub.backend.ai.dto.response.AiResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AiServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AiServiceImpl aiService;

    @Test
    void shouldReceiveResponseFromAI() {

        AiRequestDto requestDto = new AiRequestDto("How can I improve boxing stamina?");

        Map<String, Object> mockResponse =
                Map.of("choices",
                        List.of(
                                Map.of("message",
                                        Map.of("content", "Train intervals and shadowboxing.")
                                )
                        )
                );

        ReflectionTestUtils.setField(aiService, "groqUrl", "https://api.groq.com/openai");
        ReflectionTestUtils.setField(aiService, "groqApiKey", "test-key");
        ReflectionTestUtils.setField(aiService, "groqModel", "test-model");

        when(webClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(anyString()))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.header(anyString(), anyString()))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.bodyValue(any()))
                .thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.bodyToMono(
                ArgumentMatchers
                        .<ParameterizedTypeReference<Map<String, Object>>>any()
        ))
                .thenReturn(
                        reactor.core.publisher.Mono.just(mockResponse)
                );

        AiResponseDto response = aiService.askQuestion(requestDto);

        assertEquals("Train intervals and shadowboxing.", response.answer());

        verify(webClient).post();
        verify(requestBodyUriSpec).uri(anyString());
        verify(requestHeadersSpec).retrieve();
    }
}