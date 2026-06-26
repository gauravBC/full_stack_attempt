package com.nurtureai.schedule;

import com.nurtureai.auth.InvalidCredentialsException;
import com.nurtureai.schedule.NutritionScheduleController.NutritionScheduleRequest;
import com.nurtureai.schedule.NutritionScheduleController.NutritionScheduleResponse;
import com.nurtureai.schedule.NutritionScheduleController.NutritionSlotRequest;
import com.nurtureai.schedule.NutritionScheduleController.NutritionSlotResponse;
import com.nurtureai.schedule.NutritionScheduleController.NutritionSlotUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NutritionScheduleService {

    private final JdbcTemplate jdbcTemplate;

    public NutritionScheduleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public NutritionScheduleResponse getSchedule(String username) {
        String userId = getUserId(username);
        List<NutritionSlotResponse> slots = jdbcTemplate.query(
            """
                select id::text, slot_time, title, foods, calories, protein_grams,
                       reminder_enabled, completed, sort_order
                from nutrition_schedule_slots
                where user_id = ?::uuid
                order by sort_order, slot_time
                """,
            (resultSet, rowNumber) -> new NutritionSlotResponse(
                resultSet.getString("id"),
                resultSet.getString("slot_time"),
                resultSet.getString("title"),
                resultSet.getString("foods"),
                (Integer) resultSet.getObject("calories"),
                (Integer) resultSet.getObject("protein_grams"),
                resultSet.getBoolean("reminder_enabled"),
                resultSet.getBoolean("completed"),
                resultSet.getInt("sort_order")
            ),
            userId
        );

        int totalCalories = slots.stream().map(NutritionSlotResponse::calories).filter(value -> value != null).mapToInt(Integer::intValue).sum();
        int totalProtein = slots.stream().map(NutritionSlotResponse::proteinGrams).filter(value -> value != null).mapToInt(Integer::intValue).sum();
        int completedSlots = (int) slots.stream().filter(slot -> Boolean.TRUE.equals(slot.completed())).count();

        return new NutritionScheduleResponse(username, totalCalories, totalProtein, completedSlots, slots);
    }

    @Transactional
    public NutritionScheduleResponse saveSchedule(NutritionScheduleRequest request) {
        String userId = getUserId(request.username());
        jdbcTemplate.update("delete from nutrition_schedule_slots where user_id = ?::uuid", userId);

        List<NutritionSlotRequest> slots = request.slots() == null ? List.of() : request.slots();
        for (NutritionSlotRequest slot : slots) {
            jdbcTemplate.update(
                """
                    insert into nutrition_schedule_slots (
                        id, user_id, slot_time, title, foods, calories, protein_grams,
                        reminder_enabled, completed, sort_order, updated_at
                    ) values (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?, ?, now())
                    """,
                UUID.randomUUID().toString(),
                userId,
                slot.time(),
                slot.title(),
                slot.foods(),
                slot.calories(),
                slot.proteinGrams(),
                slot.reminderEnabled(),
                slot.completed(),
                slot.sortOrder()
            );
        }

        return getSchedule(request.username());
    }

    @Transactional
    public NutritionSlotResponse updateSlot(String slotId, NutritionSlotUpdateRequest request) {
        String userId = getUserId(request.username());
        jdbcTemplate.update(
            """
                update nutrition_schedule_slots
                set reminder_enabled = coalesce(?, reminder_enabled),
                    completed = coalesce(?, completed),
                    updated_at = now()
                where id = ?::uuid and user_id = ?::uuid
                """,
            request.reminderEnabled(),
            request.completed(),
            slotId,
            userId
        );

        List<NutritionSlotResponse> slots = jdbcTemplate.query(
            """
                select id::text, slot_time, title, foods, calories, protein_grams,
                       reminder_enabled, completed, sort_order
                from nutrition_schedule_slots
                where id = ?::uuid and user_id = ?::uuid
                """,
            (resultSet, rowNumber) -> new NutritionSlotResponse(
                resultSet.getString("id"),
                resultSet.getString("slot_time"),
                resultSet.getString("title"),
                resultSet.getString("foods"),
                (Integer) resultSet.getObject("calories"),
                (Integer) resultSet.getObject("protein_grams"),
                resultSet.getBoolean("reminder_enabled"),
                resultSet.getBoolean("completed"),
                resultSet.getInt("sort_order")
            ),
            slotId,
            userId
        );

        if (slots.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        return slots.get(0);
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
