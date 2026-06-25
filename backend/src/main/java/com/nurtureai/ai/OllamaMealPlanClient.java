package com.nurtureai.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurtureai.mealplan.dto.DailyMealPlanResponse;
import com.nurtureai.mealplan.dto.Meal;
import com.nurtureai.mealplan.dto.NutrientGoal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

class OllamaMealPlanClient implements MealPlanAiClient {

    private final AiProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    OllamaMealPlanClient(AiProperties properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    @Override
    public DailyMealPlanResponse generateDailyPlan(AiMealPlanRequest request) {
        Map<String, Object> body = Map.of(
            "model", properties.ollamaModel(),
            "stream", false,
            "format", "json",
            "messages", List.of(
                Map.of("role", "system", "content", developerPrompt()),
                Map.of("role", "user", "content", userPrompt(request))
            )
        );

        try {
            String response = restClient.post()
                .uri(properties.ollamaUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

            return parseMealPlan(request.userId(), request.pregnancyWeek(), extractContent(response));
        } catch (RestClientResponseException exception) {
            throw new AiProviderException(resolveStatus(exception), "Ollama request failed with status " + exception.getStatusCode().value() + ".");
        } catch (ResourceAccessException exception) {
            throw new AiProviderException(HttpStatus.BAD_GATEWAY, "Ollama is not running. Start Ollama locally, pull " + properties.ollamaModel() + ", then restart the backend.");
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
            throw new IllegalStateException("Unable to serialize Ollama meal plan request", exception);
        }
    }

    private String extractContent(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("message").path("content").asText();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to parse Ollama response", exception);
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
                objectMapper.readerForListOf(NutrientGoal.class).readValue(root.path("nutrients")),
                objectMapper.readerForListOf(Meal.class).readValue(root.path("meals")),
                objectMapper.readerForListOf(String.class).readValue(root.path("groceryList")),
                objectMapper.readerForListOf(String.class).readValue(root.path("reminders")),
                root.path("partnerTask").asText("Ask your partner to help prepare one meal today."),
                objectMapper.readerForListOf(String.class).readValue(root.path("safetyNotes"))
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Ollama returned invalid meal plan JSON", exception);
        }
    }

    private HttpStatus resolveStatus(RestClientResponseException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        return status == null ? HttpStatus.BAD_GATEWAY : status;
    }
}
