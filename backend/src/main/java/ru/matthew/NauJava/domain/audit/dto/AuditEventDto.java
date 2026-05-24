package ru.matthew.NauJava.domain.audit.dto;

import ru.matthew.NauJava.domain.audit.EventType;

public record AuditEventDto(
        Long userId,
        EventType eventType,
        String description
) {
}
