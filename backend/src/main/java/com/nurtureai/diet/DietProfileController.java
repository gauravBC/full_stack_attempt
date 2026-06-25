package com.nurtureai.diet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diet-profile")
public class DietProfileController {

    private final DietProfileService dietProfileService;

    public DietProfileController(DietProfileService dietProfileService) {
        this.dietProfileService = dietProfileService;
    }

    @GetMapping
    DietProfileResponse getProfile(@RequestParam @NotBlank String username) {
        return dietProfileService.getProfile(username);
    }

    @PutMapping
    DietProfileResponse saveProfile(@Valid @RequestBody DietProfileRequest request) {
        return dietProfileService.saveProfile(request);
    }

    public record DietProfileRequest(
        @NotBlank String username,
        @Min(13) @Max(60) Integer age,
        @DecimalMin("90.0") @DecimalMax("230.0") BigDecimal heightCm,
        @DecimalMin("30.0") @DecimalMax("250.0") BigDecimal weightKg,
        @Min(1) @Max(42) int pregnancyWeek,
        @NotBlank String foodPreference,
        boolean eggsAllowed,
        String allergies,
        @NotBlank String cuisineRegion,
        @NotBlank String budgetLevel
    ) {
    }

    public record DietProfileResponse(
        String id,
        String username,
        Integer age,
        BigDecimal heightCm,
        BigDecimal weightKg,
        int pregnancyWeek,
        String foodPreference,
        boolean eggsAllowed,
        String allergies,
        String cuisineRegion,
        String budgetLevel
    ) {
    }
}
