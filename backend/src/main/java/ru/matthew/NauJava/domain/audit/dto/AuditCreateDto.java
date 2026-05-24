package ru.matthew.NauJava.domain.audit.dto;

import ru.matthew.NauJava.domain.audit.EventType;

public record AuditCreateDto(
        Long userId,
        EventType eventType,
        String userAgent,
        String description
) {
}
