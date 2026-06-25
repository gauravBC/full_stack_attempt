package com.nurtureai.mealplan;

import com.nurtureai.mealplan.dto.DailyMealPlanResponse;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/daily-plans")
public class DailyMealPlanController {

    private final MealPlanService mealPlanService;

    public DailyMealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @GetMapping("/today")
    DailyMealPlanResponse today(@RequestParam @NotBlank String userId) {
        return mealPlanService.getTodayPlan(userId);
    }

    @PostMapping("/generate")
    GenerationResponse generate(@RequestParam @NotBlank String userId) {
        String jobId = mealPlanService.queuePlanGeneration(userId);
        return new GenerationResponse(jobId, "meal-plan.generate");
    }

    @PostMapping("/generate-now")
    DailyMealPlanResponse generateNow(@RequestParam @NotBlank String userId) {
        return mealPlanService.generateTodayPlan(userId);
    }

    @PutMapping("/grocery-list")
    DailyMealPlanResponse updateGroceryList(@RequestBody GroceryListRequest request) {
        return mealPlanService.updateGroceryList(request.username(), request.items());
    }

    record GroceryListRequest(@NotBlank String username, List<String> items) {
    }

    record GenerationResponse(String jobId, String topic) {
    }
}
