package com.nurtureai.mealplan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurtureai.ai.AiMealPlanRequest;
import com.nurtureai.ai.AiOrchestrator;
import com.nurtureai.auth.InvalidCredentialsException;
import com.nurtureai.diet.DietProfileController.DietProfileResponse;
import com.nurtureai.diet.DietProfileService;
import com.nurtureai.mealplan.dto.DailyMealPlanResponse;
import com.nurtureai.mealplan.dto.Meal;
import com.nurtureai.mealplan.dto.NutrientGoal;
import com.nurtureai.pantry.PantryService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MealPlanService {

    private final AiOrchestrator aiOrchestrator;
    private final DietProfileService dietProfileService;
    private final PantryService pantryService;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public MealPlanService(
        AiOrchestrator aiOrchestrator,
        DietProfileService dietProfileService,
        PantryService pantryService,
        JdbcTemplate jdbcTemplate,
        ObjectMapper objectMapper
    ) {
        this.aiOrchestrator = aiOrchestrator;
        this.dietProfileService = dietProfileService;
        this.pantryService = pantryService;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public DailyMealPlanResponse getTodayPlan(String username) {
        List<DailyMealPlanResponse> savedPlans = jdbcTemplate.query(
            """
                select mp.id::text, u.username, mp.plan_date, mp.pregnancy_week, mp.hydration_goal,
                       mp.nutrients::text, mp.meals::text, mp.grocery_list::text, mp.reminders::text,
                       mp.partner_task, mp.safety_notes::text
                from daily_meal_plans mp
                join users u on u.id = mp.user_id
                where u.username = ? and mp.plan_date = current_date
                """,
            (resultSet, rowNumber) -> new DailyMealPlanResponse(
                resultSet.getString("id"),
                resultSet.getString("username"),
                resultSet.getDate("plan_date").toLocalDate(),
                resultSet.getInt("pregnancy_week"),
                resultSet.getString("hydration_goal"),
                readList(resultSet.getString("nutrients"), new TypeReference<List<NutrientGoal>>() {}),
                readList(resultSet.getString("meals"), new TypeReference<List<Meal>>() {}),
                readList(resultSet.getString("grocery_list"), new TypeReference<List<String>>() {}),
                readList(resultSet.getString("reminders"), new TypeReference<List<String>>() {}),
                resultSet.getString("partner_task"),
                readList(resultSet.getString("safety_notes"), new TypeReference<List<String>>() {})
            ),
            username
        );

        if (!savedPlans.isEmpty()) {
            return savedPlans.get(0);
        }

        return fallbackPlan(username);
    }

    public String queuePlanGeneration(String userId) {
        String jobId = UUID.randomUUID().toString();
        aiOrchestrator.requestDailyPlan(userId, jobId);
        return jobId;
    }

    @Transactional
    public DailyMealPlanResponse generateTodayPlan(String username) {
        DietProfileResponse profile = dietProfileService.getProfile(username);
        List<String> pantryItems = pantryService.getItems(username);
        DailyMealPlanResponse generatedPlan = aiOrchestrator.generateDailyPlan(new AiMealPlanRequest(
            username,
            profile.pregnancyWeek(),
            profile.age(),
            profile.heightCm() == null ? null : profile.heightCm().toPlainString(),
            profile.weightKg() == null ? null : profile.weightKg().toPlainString(),
            profile.foodPreference(),
            profile.eggsAllowed(),
            profile.allergies(),
            profile.cuisineRegion(),
            profile.budgetLevel(),
            pantryItems,
            List.of("Avoid raw or unpasteurized foods", "Avoid listed allergens: " + profile.allergies()),
            List.of("Higher iron", "Protein support", "Hydration")
        ));

        return savePlan(username, generatedPlan);
    }

    @Transactional
    public DailyMealPlanResponse updateGroceryList(String username, List<String> groceryList) {
        DailyMealPlanResponse currentPlan = getTodayPlan(username);
        DailyMealPlanResponse updatedPlan = new DailyMealPlanResponse(
            currentPlan.id(),
            currentPlan.userId(),
            currentPlan.planDate(),
            currentPlan.pregnancyWeek(),
            currentPlan.hydrationGoal(),
            currentPlan.nutrients(),
            currentPlan.meals(),
            normalize(groceryList),
            currentPlan.reminders(),
            currentPlan.partnerTask(),
            currentPlan.safetyNotes()
        );

        return savePlan(username, updatedPlan);
    }

    private DailyMealPlanResponse savePlan(String username, DailyMealPlanResponse plan) {
        String userId = getUserId(username);
        String planId = plan.id() == null ? UUID.randomUUID().toString() : plan.id();

        jdbcTemplate.update(
            """
                insert into daily_meal_plans (
                    id, user_id, plan_date, pregnancy_week, hydration_goal, meals, nutrients,
                    grocery_list, reminders, partner_task, safety_notes
                ) values (?::uuid, ?::uuid, ?, ?, ?, ?::jsonb, ?::jsonb, ?::jsonb, ?::jsonb, ?, ?::jsonb)
                on conflict (user_id, plan_date) do update set
                    pregnancy_week = excluded.pregnancy_week,
                    hydration_goal = excluded.hydration_goal,
                    meals = excluded.meals,
                    nutrients = excluded.nutrients,
                    grocery_list = excluded.grocery_list,
                    reminders = excluded.reminders,
                    partner_task = excluded.partner_task,
                    safety_notes = excluded.safety_notes
                """,
            planId,
            userId,
            plan.planDate() == null ? LocalDate.now() : plan.planDate(),
            plan.pregnancyWeek(),
            plan.hydrationGoal(),
            writeJson(plan.meals()),
            writeJson(plan.nutrients()),
            writeJson(plan.groceryList()),
            writeJson(plan.reminders()),
            plan.partnerTask(),
            writeJson(plan.safetyNotes())
        );

        return getTodayPlan(username);
    }

    private DailyMealPlanResponse fallbackPlan(String username) {
        DietProfileResponse profile = dietProfileService.getProfile(username);
        List<String> pantryItems = pantryService.getItems(username);
        boolean canUseEggs = Boolean.TRUE.equals(profile.eggsAllowed()) && !"vegetarian_only".equals(profile.foodPreference());

        return new DailyMealPlanResponse(
            null,
            username,
            LocalDate.now(),
            profile.pregnancyWeek(),
            "2.7 L",
            List.of(
                new NutrientGoal("Protein", "75 g", "On track"),
                new NutrientGoal("Iron", "27 mg", "Needs focus"),
                new NutrientGoal("Calcium", "1,000 mg", "On track")
            ),
            List.of(
                new Meal("Breakfast", canUseEggs ? "Vegetable oats with boiled eggs" : "Vegetable oats with nuts", "Based on your saved diet profile and pantry."),
                new Meal("Lunch", "Spinach dal with brown rice", "Iron-rich meal. Add lemon for absorption."),
                new Meal("Snack", "Fruit with yogurt", "Calcium support away from iron supplement time."),
                new Meal("Dinner", "Paneer curry with chapati", "Matched to your saved food preference.")
            ),
            List.of("Milk", "Oranges", "Chickpeas", "Almonds"),
            List.of("Iron tablet at 4 PM, away from dairy.", "Short walk after dinner if approved by your doctor."),
            "Prepare dinner tonight and refill the water bottle before bedtime.",
            List.of("Pantry considered: " + String.join(", ", pantryItems), "Informational guidance only. Consult your clinician for medical questions.")
        );
    }

    private List<String> normalize(List<String> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
            .map(String::trim)
            .filter(item -> !item.isBlank())
            .distinct()
            .toList();
    }

    private String getUserId(String username) {
        return jdbcTemplate.query(
            "select id::text from users where username = ?",
            resultSet -> {
                if (!resultSet.next()) {
                    throw new InvalidCredentialsException();
                }
                return resultSet.getString("id");
            },
            username
        );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize meal plan", exception);
        }
    }

    private <T> T readList(String value, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to read saved meal plan", exception);
        }
    }
}
