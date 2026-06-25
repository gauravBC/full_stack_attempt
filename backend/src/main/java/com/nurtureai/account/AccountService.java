package com.nurtureai.account;

import com.nurtureai.account.AccountController.PartnerContactRequest;
import com.nurtureai.account.AccountController.PartnerContactResponse;
import com.nurtureai.account.AccountController.PasswordUpdateRequest;
import com.nurtureai.auth.InvalidCredentialsException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final JdbcTemplate jdbcTemplate;

    public AccountService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        UserPassword user = getUserPassword(request.username());

        if (!matches(request.currentPassword(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        jdbcTemplate.update(
            "update users set password_hash = ? where id = ?::uuid",
            "sha256:" + sha256(request.newPassword()),
            user.id()
        );
    }

    public List<PartnerContactResponse> getPartners(String username) {
        String userId = getUserId(username);
        return jdbcTemplate.query(
            """
                select id::text, first_name, last_name, email, phone_number, role, notifications_enabled
                from family_members
                where user_id = ?::uuid
                order by created_at desc
                """,
            (resultSet, rowNumber) -> new PartnerContactResponse(
                resultSet.getString("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("email"),
                resultSet.getString("phone_number"),
                resultSet.getString("role"),
                resultSet.getBoolean("notifications_enabled")
            ),
            userId
        );
    }

    @Transactional
    public PartnerContactResponse savePartner(PartnerContactRequest request) {
        String userId = getUserId(request.username());
        List<PartnerContactResponse> existing = getPartners(request.username());

        if (existing.isEmpty()) {
            String id = UUID.randomUUID().toString();
            jdbcTemplate.update(
                """
                    insert into family_members (
                        id, user_id, email, role, access_level, first_name, last_name, phone_number, notifications_enabled
                    ) values (?::uuid, ?::uuid, ?, ?, 'notification_recipient', ?, ?, ?, ?)
                    """,
                id,
                userId,
                request.email(),
                request.relationship(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                request.notificationsEnabled()
            );
            return new PartnerContactResponse(
                id,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.relationship(),
                request.notificationsEnabled()
            );
        }

        PartnerContactResponse partner = existing.get(0);
        jdbcTemplate.update(
            """
                update family_members
                set first_name = ?, last_name = ?, email = ?, phone_number = ?, role = ?, notifications_enabled = ?
                where id = ?::uuid
                """,
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phoneNumber(),
            request.relationship(),
            request.notificationsEnabled(),
            partner.id()
        );

        return new PartnerContactResponse(
            partner.id(),
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phoneNumber(),
            request.relationship(),
            request.notificationsEnabled()
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

    private UserPassword getUserPassword(String username) {
        return jdbcTemplate.query(
            "select id::text, password_hash from users where username = ?",
            resultSet -> {
                if (!resultSet.next()) {
                    throw new InvalidCredentialsException();
                }
                return new UserPassword(resultSet.getString("id"), resultSet.getString("password_hash"));
            },
            username
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

    private record UserPassword(String id, String passwordHash) {
    }
}
