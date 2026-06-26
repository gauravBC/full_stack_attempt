package com.nurtureai.hydration;

import com.nurtureai.auth.InvalidCredentialsException;
import com.nurtureai.hydration.HydrationController.HydrationLogRequest;
import com.nurtureai.hydration.HydrationController.HydrationPlanRequest;
import com.nurtureai.hydration.HydrationController.HydrationPlanResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HydrationService {

    private final JdbcTemplate jdbcTemplate;

    public HydrationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public HydrationPlanResponse getPlan(String username) {
        String userId = getUserId(username);
        List<HydrationPlanResponse> plans = jdbcTemplate.query(
            """
                select daily_goal_ml, current_intake_ml, reminder_gap_minutes, detox_recipe_title,
                       detox_ingredients, detox_steps, best_time, reminder_enabled
                from hydration_plans
                where user_id = ?::uuid
                """,
            (resultSet, rowNumber) -> toResponse(
                username,
                resultSet.getInt("daily_goal_ml"),
                resultSet.getInt("current_intake_ml"),
                resultSet.getInt("reminder_gap_minutes"),
                resultSet.getString("detox_recipe_title"),
                resultSet.getString("detox_ingredients"),
                resultSet.getString("detox_steps"),
                resultSet.getString("best_time"),
                resultSet.getBoolean("reminder_enabled")
            ),
            userId
        );

        if (!plans.isEmpty()) {
            return plans.get(0);
        }

        jdbcTemplate.update(
            """
                insert into hydration_plans (id, user_id, daily_goal_ml, current_intake_ml, reminder_gap_minutes)
                values (?::uuid, ?::uuid, 2700, 0, 90)
                """,
            UUID.randomUUID().toString(),
            userId
        );

        return getPlan(username);
    }

    @Transactional
    public HydrationPlanResponse savePlan(HydrationPlanRequest request) {
        String userId = getUserId(request.username());
        jdbcTemplate.update(
            """
                insert into hydration_plans (
                    id, user_id, daily_goal_ml, current_intake_ml, reminder_gap_minutes,
                    detox_recipe_title, detox_ingredients, detox_steps, best_time, reminder_enabled, updated_at
                ) values (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, now())
                on conflict (user_id) do update set
                    daily_goal_ml = excluded.daily_goal_ml,
                    current_intake_ml = excluded.current_intake_ml,
                    reminder_gap_minutes = excluded.reminder_gap_minutes,
                    detox_recipe_title = excluded.detox_recipe_title,
                    detox_ingredients = excluded.detox_ingredients,
                    detox_steps = excluded.detox_steps,
                    best_time = excluded.best_time,
                    reminder_enabled = excluded.reminder_enabled,
                    updated_at = now()
                """,
            UUID.randomUUID().toString(),
            userId,
            request.dailyGoalMl(),
            request.currentIntakeMl(),
            request.reminderGapMinutes(),
            request.detoxRecipeTitle(),
            request.detoxIngredients(),
            request.detoxSteps(),
            request.bestTime(),
            request.reminderEnabled()
        );

        return getPlan(request.username());
    }

    @Transactional
    public HydrationPlanResponse logWater(HydrationLogRequest request) {
        String userId = getUserId(request.username());
        getPlan(request.username());
        jdbcTemplate.update(
            "insert into hydration_logs (id, user_id, amount_ml) values (?::uuid, ?::uuid, ?)",
            UUID.randomUUID().toString(),
            userId,
            request.amountMl()
        );
        jdbcTemplate.update(
            """
                update hydration_plans
                set current_intake_ml = current_intake_ml + ?, updated_at = now()
                where user_id = ?::uuid
                """,
            request.amountMl(),
            userId
        );

        return getPlan(request.username());
    }

    private HydrationPlanResponse toResponse(
        String username,
        int dailyGoalMl,
        int currentIntakeMl,
        int reminderGapMinutes,
        String detoxRecipeTitle,
        String detoxIngredients,
        String detoxSteps,
        String bestTime,
        boolean reminderEnabled
    ) {
        int progress = dailyGoalMl <= 0 ? 0 : Math.min(100, Math.round((currentIntakeMl * 100.0f) / dailyGoalMl));
        return new HydrationPlanResponse(
            username,
            dailyGoalMl,
            currentIntakeMl,
            progress,
            reminderGapMinutes,
            detoxRecipeTitle,
            detoxIngredients,
            detoxSteps,
            bestTime,
            reminderEnabled
        );
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
}
