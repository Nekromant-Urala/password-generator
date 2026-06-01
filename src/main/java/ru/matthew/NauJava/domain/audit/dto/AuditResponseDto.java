package ru.matthew.NauJava.domain.audit.dto;

import ru.matthew.NauJava.domain.audit.EventType;

import java.time.LocalDateTime;

public record AuditResponseDto(
        Long id,
        Long userId,
        EventType type,
        String description,
        String userAgent,
        LocalDateTime createdAt
) {
}
