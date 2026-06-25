package com.nurtureai.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurtureai.mealplan.dto.DailyMealPlanResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

class OpenAiMealPlanClient implements MealPlanAiClient {

    private final AiProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    OpenAiMealPlanClient(AiProperties properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    @Override
    public DailyMealPlanResponse generateDailyPlan(AiMealPlanRequest request) {
        Map<String, Object> body = Map.of(
            "model", properties.model(),
            "input", List.of(
                Map.of(
                    "role", "developer",
                    "content", List.of(Map.of("type", "input_text", "text", developerPrompt()))
                ),
                Map.of(
                    "role", "user",
                    "content", List.of(Map.of("type", "input_text", "text", userPrompt(request)))
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

            String outputText = extractOutputText(response);
            return parseMealPlan(request.userId(), request.pregnancyWeek(), outputText);
        } catch (RestClientResponseException exception) {
            throw new AiProviderException(resolveStatus(exception), resolveMessage(exception));
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

    private String extractOutputText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode output = root.path("output");

            for (JsonNode item : output) {
                for (JsonNode content : item.path("content")) {
                    if ("output_text".equals(content.path("type").asText())) {
                        return content.path("text").asText();
                    }
                }
            }
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to parse OpenAI response", exception);
        }

        throw new IllegalStateException("OpenAI response did not include output_text");
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
                objectMapper.readerForListOf(com.nurtureai.mealplan.dto.NutrientGoal.class)
                    .readValue(root.path("nutrients")),
                objectMapper.readerForListOf(com.nurtureai.mealplan.dto.Meal.class)
                    .readValue(root.path("meals")),
                objectMapper.readerForListOf(String.class).readValue(root.path("groceryList")),
                objectMapper.readerForListOf(String.class).readValue(root.path("reminders")),
                root.path("partnerTask").asText("Ask your partner to help prepare one meal today."),
                objectMapper.readerForListOf(String.class).readValue(root.path("safetyNotes"))
            );
        } catch (Exception exception) {
            throw new IllegalStateException("AI returned invalid meal plan JSON", exception);
        }
    }
}
