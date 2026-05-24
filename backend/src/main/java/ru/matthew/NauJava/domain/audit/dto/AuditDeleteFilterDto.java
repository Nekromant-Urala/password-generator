package ru.matthew.NauJava.domain.audit.dto;

import org.springframework.format.annotation.DateTimeFormat;
import ru.matthew.NauJava.domain.audit.EventType;

import java.time.LocalDateTime;

public record AuditDeleteFilterDto(
        Long userId,
        EventType eventType,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAt
) {

}
