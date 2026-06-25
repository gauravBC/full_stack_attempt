package com.nurtureai.ai;

import java.util.List;

public record AiMealPlanRequest(
    String userId,
    int pregnancyWeek,
    Integer age,
    String heightCm,
    String weightKg,
    String dietType,
    boolean eggsAllowed,
    String allergies,
    String cuisineRegion,
    String budgetLevel,
    List<String> pantryItems,
    List<String> safetyConstraints,
    List<String> nutritionGoals
) {
}
