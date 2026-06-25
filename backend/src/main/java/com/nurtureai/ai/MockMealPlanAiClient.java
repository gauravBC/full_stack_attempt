package com.nurtureai.ai;

import com.nurtureai.mealplan.dto.DailyMealPlanResponse;
import com.nurtureai.mealplan.dto.Meal;
import com.nurtureai.mealplan.dto.NutrientGoal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

class MockMealPlanAiClient implements MealPlanAiClient {

    @Override
    public DailyMealPlanResponse generateDailyPlan(AiMealPlanRequest request) {
        return new DailyMealPlanResponse(
            UUID.randomUUID().toString(),
            request.userId(),
            LocalDate.now(),
            request.pregnancyWeek(),
            "2.7 L",
            List.of(
                new NutrientGoal("Protein", "75 g", "On track"),
                new NutrientGoal("Iron", "27 mg", "Needs focus"),
                new NutrientGoal("Calcium", "1,000 mg", "On track")
            ),
            List.of(
                new Meal("Breakfast", request.eggsAllowed() ? "Vegetable oats with boiled eggs" : "Vegetable oats with nuts", "Selected from your diet profile, pantry, and allergy constraints."),
                new Meal("Lunch", "Spinach dal with brown rice", "Iron-focused meal for week " + request.pregnancyWeek() + ". Add lemon to support absorption."),
                new Meal("Snack", "Fruit with yogurt", "Calcium support planned around your daily nutrition goals."),
                new Meal("Dinner", request.dietType().contains("non_veg") ? "Chicken curry with chapati" : "Paneer curry with chapati", "Matched to your food preference: " + request.dietType() + ".")
            ),
            List.of("Milk", "Oranges", "Chickpeas", "Almonds"),
            List.of("Iron tablet at 4 PM, away from dairy.", "Drink water steadily across the day."),
            "Prepare dinner tonight and encourage a short walk if approved by the doctor.",
            List.of("Profile inputs used: age " + request.age() + ", height " + request.heightCm() + " cm, weight " + request.weightKg() + " kg.", "Avoid allergens: " + request.allergies(), "Informational guidance only. Consult your clinician for medical questions.")
        );
    }
}
