package com.nurtureai.health;

import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    Map<String, Object> health() {
        Integer databaseOk = jdbcTemplate.queryForObject("select 1", Integer.class);
        return Map.of(
            "status", "ok",
            "database", databaseOk != null && databaseOk == 1 ? "ok" : "unknown",
            "timestamp", OffsetDateTime.now().toString()
        );
    }
}
