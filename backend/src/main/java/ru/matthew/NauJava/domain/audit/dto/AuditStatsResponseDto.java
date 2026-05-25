package ru.matthew.NauJava.domain.audit.dto;

public record AuditStatsResponseDto(
        long totalEvent,
        long totalUser,
        long totalEntries,
        long totalUsersForLastDay
) {
}
