package ru.matthew.NauJava.domain.audit.dto;

import ru.matthew.NauJava.domain.audit.EventType;

import java.time.LocalDateTime;

public record AuditResponseDto(
        Long id,
        EventType type,
        String description,
        String userAgent,
        LocalDateTime createdAt
) {
}
