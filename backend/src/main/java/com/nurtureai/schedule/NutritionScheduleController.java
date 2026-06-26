package com.nurtureai.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/nutrition-schedule")
public class NutritionScheduleController {

    private final NutritionScheduleService nutritionScheduleService;

    public NutritionScheduleController(NutritionScheduleService nutritionScheduleService) {
        this.nutritionScheduleService = nutritionScheduleService;
    }

    @GetMapping
    NutritionScheduleResponse schedule(@RequestParam @NotBlank String username) {
        return nutritionScheduleService.getSchedule(username);
    }

    @PutMapping
    NutritionScheduleResponse saveSchedule(@RequestBody @Valid NutritionScheduleRequest request) {
        return nutritionScheduleService.saveSchedule(request);
    }

    @PatchMapping("/slots/{slotId}")
    NutritionSlotResponse updateSlot(@PathVariable String slotId, @RequestBody @Valid NutritionSlotUpdateRequest request) {
        return nutritionScheduleService.updateSlot(slotId, request);
    }

    public record NutritionScheduleResponse(
        String username,
        Integer totalCalories,
        Integer totalProteinGrams,
        Integer completedSlots,
        List<NutritionSlotResponse> slots
    ) {
    }

    public record NutritionSlotResponse(
        String id,
        String time,
        String title,
        String foods,
        Integer calories,
        Integer proteinGrams,
        Boolean reminderEnabled,
        Boolean completed,
        Integer sortOrder
    ) {
    }

    public record NutritionScheduleRequest(@NotBlank String username, List<@Valid NutritionSlotRequest> slots) {
    }

    public record NutritionSlotRequest(
        String id,
        @NotBlank String time,
        @NotBlank String title,
        @NotBlank String foods,
        @PositiveOrZero Integer calories,
        @PositiveOrZero Integer proteinGrams,
        @NotNull Boolean reminderEnabled,
        @NotNull Boolean completed,
        @NotNull Integer sortOrder
    ) {
    }

    public record NutritionSlotUpdateRequest(
        @NotBlank String username,
        Boolean reminderEnabled,
        Boolean completed
    ) {
    }
}
