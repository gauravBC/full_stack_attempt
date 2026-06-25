package com.nurtureai.pantry;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    private final PantryService pantryService;

    public PantryController(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    @GetMapping
    List<String> pantry(@RequestParam @NotBlank String username) {
        return pantryService.getItems(username);
    }

    @PutMapping
    List<String> savePantry(@RequestBody PantryRequest request) {
        return pantryService.saveItems(request.username(), request.items());
    }

    record PantryRequest(@NotBlank String username, List<String> items) {
    }
}
