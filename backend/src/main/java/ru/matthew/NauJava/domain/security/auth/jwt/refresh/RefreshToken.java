package ru.matthew.NauJava.domain.security.auth.jwt.refresh;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RefreshToken(
        UUID id, // всегда новый идентификатор
        String subject,
        List<String> authorities,
        Instant createdAt,
        Instant expiresAt
) {
}
