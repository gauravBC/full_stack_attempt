package com.nurtureai.pantry;

import com.nurtureai.auth.InvalidCredentialsException;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PantryService {

    private static final List<String> DEFAULT_ITEMS = List.of("Spinach", "Rice", "Paneer", "Oats", "Eggs");

    private final JdbcTemplate jdbcTemplate;

    public PantryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getItems(String username) {
        String userId = getUserId(username);
        List<String> items = jdbcTemplate.query(
            "select name from pantry_items where user_id = ?::uuid order by created_at, name",
            (resultSet, rowNumber) -> resultSet.getString("name"),
            userId
        );

        return items.isEmpty() ? DEFAULT_ITEMS : items;
    }

    @Transactional
    public List<String> saveItems(String username, List<String> items) {
        String userId = getUserId(username);
        jdbcTemplate.update("delete from pantry_items where user_id = ?::uuid", userId);

        for (String item : normalize(items)) {
            jdbcTemplate.update(
                "insert into pantry_items (id, user_id, name) values (?::uuid, ?::uuid, ?)",
                UUID.randomUUID().toString(),
                userId,
                item
            );
        }

        return getItems(username);
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
}
