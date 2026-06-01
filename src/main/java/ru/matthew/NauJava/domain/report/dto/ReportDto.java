package ru.matthew.NauJava.domain.report.dto;

import ru.matthew.NauJava.domain.report.ReportStatus;

import java.time.LocalDateTime;

public record ReportDto(
        Long id,
        ReportStatus status,
        LocalDateTime createdAt,
        String description
) {
}
