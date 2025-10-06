package com.techSuggestion.bot.service;

import com.techSuggestion.bot.dto.OpenAiChatRequest;
import com.techSuggestion.bot.dto.ProductFilter;
import com.techSuggestion.bot.models.Category;
import com.techSuggestion.bot.models.Product;
import com.techSuggestion.bot.pojo.AiResponse;
import com.techSuggestion.bot.repository.CategoryRepository;
import com.techSuggestion.bot.repository.ProductRepository;
import com.techSuggestion.bot.repository.SearchLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenAiServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SearchLogRepository searchLogRepository;

    @InjectMocks
    private OpenAiService openAiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Parse user prompt successfully")
    void parseUserPromptSuccessfully() {
        String userPrompt = "Find products under $500";
        String mockResponse = "{\"id\":\"123\",\"model\":\"sonar-reasoning\",\"choices\":[{\"message\":{\"content\":\"{\\\"productFilter\\\":{},\\\"products\\\":[]}\"}}]}";

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        Mono<AiResponse> response = openAiService.parseUserPrompt(userPrompt);

        AiResponse aiResponse = response.blockOptional().orElse(null);
        assertNotNull(aiResponse);
        assertEquals("123", aiResponse.getId());
        assertEquals("sonar-reasoning", aiResponse.getModel());
        assertNotNull(aiResponse.getProductFilter());
        assertTrue(aiResponse.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Handle invalid AI response gracefully")
    void handleInvalidAiResponseGracefully() {
        String userPrompt = "Find products under $500";
        String invalidResponse = "Invalid JSON";

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidResponse));

        Mono<AiResponse> response = openAiService.parseUserPrompt(userPrompt);

        AiResponse aiResponse = response.blockOptional().orElse(null);
        assertNotNull(aiResponse);
        assertEquals("Error parsing AI response.", aiResponse.getRawContent());
        assertNotNull(aiResponse.getProductFilter());
        assertTrue(aiResponse.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Send request and receive response successfully")
    void sendRequestAndReceiveResponseSuccessfully() throws Exception {
        OpenAiChatRequest request = OpenAiChatRequest.builder()
                .model("sonar-reasoning")
                .messages(Collections.emptyList())
                .max_tokens(500)
                .temperature(0.7)
                .build();
        String mockResponse = "{\"id\":\"123\",\"model\":\"sonar-reasoning\"}";

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        Mono<String> response = openAiService.sendRequest(request);

        assertEquals(mockResponse, response.block());
    }

    @Test
    @DisplayName("Generate product description successfully")
    void generateProductDescriptionSuccessfully() {
        String productPrompt = "Describe a flagship phone";
        String mockResponse = "A flagship phone with amazing features.";

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        Mono<String> response = openAiService.generateDescription(productPrompt);

        assertEquals(mockResponse, response.block());
    }
}
