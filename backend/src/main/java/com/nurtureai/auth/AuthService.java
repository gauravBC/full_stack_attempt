package com.nurtureai.auth;

import com.nurtureai.auth.dto.LoginRequest;
import com.nurtureai.auth.dto.SignUpRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JdbcTemplate jdbcTemplate;

    public AuthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuthenticatedUser login(LoginRequest request) {
        return jdbcTemplate.query("""
                select id::text, username, first_name, last_name, email, password_hash
                from users
                where username = ?
                """,
            resultSet -> {
                if (!resultSet.next()) {
                    throw new InvalidCredentialsException();
                }

                String expectedHash = resultSet.getString("password_hash");
                if (!matches(request.password(), expectedHash)) {
                    throw new InvalidCredentialsException();
                }

                return new AuthenticatedUser(
                    resultSet.getString("id"),
                    resultSet.getString("username"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email")
                );
            },
            request.username()
        );
    }

    public AuthenticatedUser signUp(SignUpRequest request) {
        String userId = UUID.randomUUID().toString();

        try {
            jdbcTemplate.update(
                """
                    insert into users (
                        id, email, display_name, username, first_name, last_name,
                        phone_number, password_hash
                    ) values (?::uuid, ?, ?, ?, ?, ?, ?, ?)
                    """,
                userId,
                request.email(),
                request.firstName() + " " + request.lastName(),
                request.username(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                "sha256:" + sha256(request.password())
            );
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateAccountException();
        }

        return new AuthenticatedUser(
            userId,
            request.username(),
            request.firstName(),
            request.lastName(),
            request.email()
        );
    }

    private boolean matches(String rawPassword, String storedHash) {
        if (storedHash == null || !storedHash.startsWith("sha256:")) {
            return false;
        }

        return MessageDigest.isEqual(
            storedHash.substring("sha256:".length()).getBytes(StandardCharsets.UTF_8),
            sha256(rawPassword).getBytes(StandardCharsets.UTF_8)
        );
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hash.length * 2);

            for (byte item : hash) {
                builder.append(String.format("%02x", item));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
