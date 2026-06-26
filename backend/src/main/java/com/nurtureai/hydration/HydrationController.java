package com.nurtureai.hydration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
@RequestMapping("/api/hydration")
public class HydrationController {

    private final HydrationService hydrationService;

    public HydrationController(HydrationService hydrationService) {
        this.hydrationService = hydrationService;
    }

    @GetMapping
    HydrationPlanResponse hydration(@RequestParam @NotBlank String username) {
        return hydrationService.getPlan(username);
    }

    @PutMapping
    HydrationPlanResponse savePlan(@RequestBody @Valid HydrationPlanRequest request) {
        return hydrationService.savePlan(request);
    }

    @PostMapping("/logs")
    HydrationPlanResponse logWater(@RequestBody @Valid HydrationLogRequest request) {
        return hydrationService.logWater(request);
    }

    public record HydrationPlanResponse(
        String username,
        Integer dailyGoalMl,
        Integer currentIntakeMl,
        Integer progressPercent,
        Integer reminderGapMinutes,
        String detoxRecipeTitle,
        String detoxIngredients,
        String detoxSteps,
        String bestTime,
        Boolean reminderEnabled
    ) {
    }

    public record HydrationPlanRequest(
        @NotBlank String username,
        @Positive Integer dailyGoalMl,
        @PositiveOrZero Integer currentIntakeMl,
        @Positive Integer reminderGapMinutes,
        @NotBlank String detoxRecipeTitle,
        @NotBlank String detoxIngredients,
        @NotBlank String detoxSteps,
        @NotBlank String bestTime,
        @NotNull Boolean reminderEnabled
    ) {
    }

    public record HydrationLogRequest(@NotBlank String username, @Positive Integer amountMl) {
    }
}
