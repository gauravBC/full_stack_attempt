package com.nurtureai.diet;

import com.nurtureai.auth.InvalidCredentialsException;
import com.nurtureai.diet.DietProfileController.DietProfileRequest;
import com.nurtureai.diet.DietProfileController.DietProfileResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DietProfileService {

    private final JdbcTemplate jdbcTemplate;

    public DietProfileService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DietProfileResponse getProfile(String username) {
        List<DietProfileResponse> profiles = jdbcTemplate.query(
            """
                select dp.id::text, u.username, dp.age, dp.height_cm, dp.weight_kg, dp.pregnancy_week,
                       dp.food_preference, dp.eggs_allowed, dp.allergies, dp.cuisine_region, dp.budget_level
                from users u
                left join user_diet_profiles dp on dp.user_id = u.id
                where u.username = ?
                """,
            (resultSet, rowNumber) -> {
                String id = resultSet.getString("id");
                if (id == null) {
                    return defaultProfile(username);
                }
                return new DietProfileResponse(
                    id,
                    resultSet.getString("username"),
                    (Integer) resultSet.getObject("age"),
                    resultSet.getBigDecimal("height_cm"),
                    resultSet.getBigDecimal("weight_kg"),
                    resultSet.getInt("pregnancy_week"),
                    resultSet.getString("food_preference"),
                    resultSet.getBoolean("eggs_allowed"),
                    resultSet.getString("allergies"),
                    resultSet.getString("cuisine_region"),
                    resultSet.getString("budget_level")
                );
            },
            username
        );

        if (profiles.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        return profiles.get(0);
    }

    @Transactional
    public DietProfileResponse saveProfile(DietProfileRequest request) {
        String userId = getUserId(request.username());
        DietProfileResponse existing = getProfile(request.username());
        String id = existing.id() == null ? UUID.randomUUID().toString() : existing.id();

        jdbcTemplate.update(
            """
                insert into user_diet_profiles (
                    id, user_id, age, height_cm, weight_kg, pregnancy_week, food_preference,
                    eggs_allowed, allergies, cuisine_region, budget_level, updated_at
                ) values (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())
                on conflict (user_id) do update set
                    age = excluded.age,
                    height_cm = excluded.height_cm,
                    weight_kg = excluded.weight_kg,
                    pregnancy_week = excluded.pregnancy_week,
                    food_preference = excluded.food_preference,
                    eggs_allowed = excluded.eggs_allowed,
                    allergies = excluded.allergies,
                    cuisine_region = excluded.cuisine_region,
                    budget_level = excluded.budget_level,
                    updated_at = now()
                """,
            id,
            userId,
            request.age(),
            request.heightCm(),
            request.weightKg(),
            request.pregnancyWeek(),
            request.foodPreference(),
            request.eggsAllowed(),
            request.allergies(),
            request.cuisineRegion(),
            request.budgetLevel()
        );

        return getProfile(request.username());
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

    private DietProfileResponse defaultProfile(String username) {
        return new DietProfileResponse(
            null,
            username,
            null,
            null,
            null,
            22,
            "vegetarian_with_eggs",
            true,
            "",
            "Indian",
            "medium"
        );
    }
}
