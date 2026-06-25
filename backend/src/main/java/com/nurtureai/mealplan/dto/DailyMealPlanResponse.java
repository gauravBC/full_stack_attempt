package com.nurtureai.mealplan.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyMealPlanResponse(
    String id,
    String userId,
    LocalDate planDate,
    int pregnancyWeek,
    String hydrationGoal,
    List<NutrientGoal> nutrients,
    List<Meal> meals,
    List<String> groceryList,
    List<String> reminders,
    String partnerTask,
    List<String> safetyNotes
) {
}
