package com.nurtureai.auth;

public record AuthenticatedUser(
    String id,
    String username,
    String firstName,
    String lastName,
    String email
) {
}
