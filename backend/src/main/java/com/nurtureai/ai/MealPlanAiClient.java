package com.nurtureai.ai;

import com.nurtureai.mealplan.dto.DailyMealPlanResponse;

public interface MealPlanAiClient {

    DailyMealPlanResponse generateDailyPlan(AiMealPlanRequest request);
}
