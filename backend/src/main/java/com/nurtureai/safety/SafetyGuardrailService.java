package com.nurtureai.safety;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SafetyGuardrailService {

    private static final List<String> BLOCKED_FOODS = List.of(
        "unpasteurized milk",
        "raw fish",
        "undercooked eggs"
    );

    public List<String> blockedFoods() {
        return BLOCKED_FOODS;
    }
}
