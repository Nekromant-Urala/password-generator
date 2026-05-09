package ru.matthew.NauJava.domain.security.auth.jwt.access;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AccessToken(
        UUID id, // соответствует идентификатору refresh-токена относительного, которого он выдан
        String subject,
        List<String> authorities,
        Instant createdAt,
        Instant expiresAt
) {
}
