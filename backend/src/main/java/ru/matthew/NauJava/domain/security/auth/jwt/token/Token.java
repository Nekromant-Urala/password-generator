package ru.matthew.NauJava.domain.security.auth.jwt.token;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Token(
        UUID id,
        String subject,
        List<String> authorities,
        Instant createAt,
        Instant expiresAt
) {
}
