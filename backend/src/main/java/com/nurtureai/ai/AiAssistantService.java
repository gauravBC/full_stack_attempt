package com.nurtureai.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurtureai.ai.AiAssistantController.AiAnswerResponse;
import com.nurtureai.ai.AiAssistantController.AiStatusResponse;
import com.nurtureai.ai.AiAssistantController.ChatRequest;
import com.nurtureai.ai.AiAssistantController.MealExplanationRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class AiAssistantService {

    private final AiProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAssistantService(AiProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    public AiStatusResponse status() {
        return new AiStatusResponse(properties.activeProvider(), properties.activeModel(), properties.openAiEnabled() || properties.ollamaEnabled());
    }

    public AiAnswerResponse chat(ChatRequest request) {
        if (properties.ollamaEnabled()) {
            return new AiAnswerResponse(callOllama(
                "Answer as NurtureAI, a cautious pregnancy nutrition assistant. "
                    + "Be warm, practical, concise, and never diagnose. Tell the user to consult a clinician for medical concerns.",
                "User question: " + request.message() + "\nContext: " + toJson(request)
            ));
        }

        if (!properties.openAiEnabled()) {
            return new AiAnswerResponse(mockChatAnswer(request));
        }

        return new AiAnswerResponse(callOpenAi(
            "Answer as NurtureAI, a cautious pregnancy nutrition assistant. "
                + "Be warm, practical, concise, and never diagnose. Tell the user to consult a clinician for medical concerns.",
            "User question: " + request.message() + "\nContext: " + toJson(request)
        ));
    }

    public AiAnswerResponse explainMeal(MealExplanationRequest request) {
        if (properties.ollamaEnabled()) {
            return new AiAnswerResponse(callOllama(
                "Explain why a meal may be suggested during pregnancy. Keep it practical, evidence-informed, and concise. "
                    + "Mention key nutrients, pantry fit, timing/safety considerations, and remind that this is not medical advice.",
                "Meal explanation request: " + toJson(request)
            ));
        }

        if (!properties.openAiEnabled()) {
            return new AiAnswerResponse(mockMealExplanation(request));
        }

        return new AiAnswerResponse(callOpenAi(
            "Explain why a meal may be suggested during pregnancy. Keep it practical, evidence-informed, and concise. "
                + "Mention key nutrients, pantry fit, timing/safety considerations, and remind that this is not medical advice.",
            "Meal explanation request: " + toJson(request)
        ));
    }

    private String callOpenAi(String developerPrompt, String userPrompt) {
        Map<String, Object> body = Map.of(
            "model", properties.model(),
            "input", List.of(
                Map.of(
                    "role", "developer",
                    "content", List.of(Map.of("type", "input_text", "text", developerPrompt))
                ),
                Map.of(
                    "role", "user",
                    "content", List.of(Map.of("type", "input_text", "text", userPrompt))
                )
            )
        );

        try {
            String response = restClient.post()
                .uri(properties.responsesUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

            return extractOutputText(response);
        } catch (RestClientResponseException exception) {
            throw new AiProviderException(resolveStatus(exception), resolveMessage(exception));
        }
    }



    private String callOllama(String developerPrompt, String userPrompt) {
        Map<String, Object> body = Map.of(
            "model", properties.ollamaModel(),
            "stream", false,
            "messages", List.of(
                Map.of("role", "system", "content", developerPrompt),
                Map.of("role", "user", "content", userPrompt)
            )
        );

        try {
            String response = restClient.post()
                .uri(properties.ollamaUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

            return extractOllamaContent(response);
        } catch (RestClientResponseException exception) {
            throw new AiProviderException(resolveStatus(exception), "Ollama request failed with status " + exception.getStatusCode().value() + ".");
        } catch (ResourceAccessException exception) {
            throw new AiProviderException(HttpStatus.BAD_GATEWAY, "Ollama is not running. Start Ollama locally, pull " + properties.ollamaModel() + ", then restart the backend.");
        }
    }

    private String extractOllamaContent(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("message").path("content").asText();
            if (content == null || content.isBlank()) {
                throw new IllegalStateException("Ollama response did not include message.content");
            }
            return content;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to parse Ollama assistant response", exception);
        }
    }

    private HttpStatus resolveStatus(RestClientResponseException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        return status == null ? HttpStatus.BAD_GATEWAY : status;
    }

    private String resolveMessage(RestClientResponseException exception) {
        String body = exception.getResponseBodyAsString();
        if (body.contains("insufficient_quota")) {
            return "OpenAI quota or billing is not available for this API key. Add billing/credits or use a different key, then try again.";
        }
        if (exception.getStatusCode().value() == 401) {
            return "OpenAI rejected the API key. Check or rotate the key and restart the backend.";
        }
        if (exception.getStatusCode().value() == 429) {
            return "OpenAI rate limit was reached. Wait briefly and try again.";
        }
        return "OpenAI request failed with status " + exception.getStatusCode().value() + ".";
    }

    private String extractOutputText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            for (JsonNode item : root.path("output")) {
                for (JsonNode content : item.path("content")) {
                    if ("output_text".equals(content.path("type").asText())) {
                        return content.path("text").asText();
                    }
                }
            }
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to parse AI assistant response", exception);
        }

        throw new IllegalStateException("AI assistant response did not include output_text");
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize AI assistant request", exception);
        }
    }

    private String mockChatAnswer(ChatRequest request) {
        return "I can help with pregnancy nutrition planning, pantry-based meals, grocery choices, hydration, and supplement timing. "
            + "For today, focus on steady protein, iron-rich foods with vitamin C, calcium at a separate time from iron, and regular water. "
            + "For symptoms, medication changes, or medical concerns, please check with your clinician.";
    }

    private String mockMealExplanation(MealExplanationRequest request) {
        return request.mealTitle() + " is a useful " + request.mealTime().toLowerCase()
            + " option because it supports pregnancy nutrition with familiar ingredients and a balanced mix of energy and nutrients. "
            + "It can help with protein and micronutrient coverage, while the note about the meal guides timing or absorption. "
            + "This is informational guidance, so adapt it to your clinician's advice, allergies, and tolerance.";
    }
}
