package com.nurtureai.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @GetMapping("/status")
    AiStatusResponse status() {
        return aiAssistantService.status();
    }

    @PostMapping("/chat")
    AiAnswerResponse chat(@Valid @RequestBody ChatRequest request) {
        return aiAssistantService.chat(request);
    }

    @PostMapping("/explain-meal")
    AiAnswerResponse explainMeal(@Valid @RequestBody MealExplanationRequest request) {
        return aiAssistantService.explainMeal(request);
    }

    public record ChatRequest(
        @NotBlank String message,
        int pregnancyWeek,
        List<String> pantryItems,
        List<String> safetyNotes
    ) {
    }

    public record MealExplanationRequest(
        @NotBlank String mealTime,
        @NotBlank String mealTitle,
        String mealNote,
        int pregnancyWeek,
        List<String> nutritionGoals,
        List<String> safetyNotes
    ) {
    }

    public record AiAnswerResponse(String answer) {
    }

    public record AiStatusResponse(String provider, String model, boolean realAiEnabled) {
    }
}
