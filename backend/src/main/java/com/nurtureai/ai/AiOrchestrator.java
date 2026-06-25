package com.nurtureai.ai;

import com.nurtureai.ai.events.MealPlanGenerationRequested;
import com.nurtureai.mealplan.dto.DailyMealPlanResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiOrchestrator {

    private static final String MEAL_PLAN_TOPIC = "meal-plan.generate";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MealPlanAiClient mealPlanAiClient;

    public AiOrchestrator(KafkaTemplate<String, Object> kafkaTemplate, MealPlanAiClient mealPlanAiClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.mealPlanAiClient = mealPlanAiClient;
    }

    public void requestDailyPlan(String userId, String jobId) {
        kafkaTemplate.send(MEAL_PLAN_TOPIC, userId, new MealPlanGenerationRequested(jobId, userId));
    }

    public DailyMealPlanResponse generateDailyPlan(AiMealPlanRequest request) {
        return mealPlanAiClient.generateDailyPlan(request);
    }
}
