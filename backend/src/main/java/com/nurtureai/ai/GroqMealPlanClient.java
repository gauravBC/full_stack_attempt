package com.nurtureai.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurtureai.mealplan.dto.DailyMealPlanResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

class GroqMealPlanClient implements MealPlanAiClient {

    private final AiProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    GroqMealPlanClient(AiProperties properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    @Override
    public DailyMealPlanResponse generateDailyPlan(AiMealPlanRequest request) {
        Map<String, Object> body = Map.of(
            "model", properties.groqModel(),
            "response_format", Map.of("type", "json_object"),
            "messages", List.of(
                Map.of("role", "system", "content", developerPrompt()),
                Map.of("role", "user", "content", userPrompt(request))
            )
        );

        try {
            String response = restClient.post()
                .uri(properties.groqChatUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.groqApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

            return parseMealPlan(request.userId(), request.pregnancyWeek(), extractMessageContent(response));
        } catch (RestClientResponseException exception) {
            throw new AiProviderException(resolveStatus(exception), resolveMessage(exception));
        }
    }

    private String developerPrompt() {
        return """
            You are NurtureAI, a pregnancy nutrition planning assistant.
            Return only valid JSON. Do not wrap it in markdown.
            Never diagnose medical conditions. Include a clinician-consult safety note.
            Avoid raw, unpasteurized, unsafe, or alcohol-related recommendations.
            JSON shape:
            {
              "hydrationGoal": "string",
              "nutrients": [{"label": "string", "value": "string", "status": "string"}],
              "meals": [{"time": "Breakfast|Lunch|Snack|Dinner", "title": "string", "note": "string"}],
              "groceryList": ["string"],
              "reminders": ["string"],
              "partnerTask": "string",
              "safetyNotes": ["string"]
            }
            """;
    }

    private String userPrompt(AiMealPlanRequest request) {
        try {
            return "Create today's pregnancy-safe meal plan from this profile: "
                + objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize AI meal plan request", exception);
        }
    }

    private String extractMessageContent(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").path(0).path("message").path("content").asText();
            if (content == null || content.isBlank()) {
                throw new IllegalStateException("Groq response did not include choices[0].message.content");
            }
            return content;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to parse Groq response", exception);
        }
    }

    private DailyMealPlanResponse parseMealPlan(String userId, int pregnancyWeek, String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            return new DailyMealPlanResponse(
                UUID.randomUUID().toString(),
                userId,
                LocalDate.now(),
                pregnancyWeek,
                root.path("hydrationGoal").asText("2.7 L"),
                objectMapper.readerForListOf(com.nurtureai.mealplan.dto.NutrientGoal.class).readValue(root.path("nutrients")),
                objectMapper.readerForListOf(com.nurtureai.mealplan.dto.Meal.class).readValue(root.path("meals")),
                objectMapper.readerForListOf(String.class).readValue(root.path("groceryList")),
                objectMapper.readerForListOf(String.class).readValue(root.path("reminders")),
                root.path("partnerTask").asText("Ask your partner to help prepare one meal today."),
                objectMapper.readerForListOf(String.class).readValue(root.path("safetyNotes"))
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Groq returned invalid meal plan JSON", exception);
        }
    }

    private HttpStatus resolveStatus(RestClientResponseException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        return status == null ? HttpStatus.BAD_GATEWAY : status;
    }

    private String resolveMessage(RestClientResponseException exception) {
        if (exception.getStatusCode().value() == 401) {
            return "Groq rejected the API key. Check GROQ_API_KEY and restart the backend.";
        }
        if (exception.getStatusCode().value() == 429) {
            return "Groq rate limit was reached. Wait briefly and try again.";
        }
        return "Groq request failed with status " + exception.getStatusCode().value() + ".";
    }
}
