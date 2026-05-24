package ru.matthew.NauJava.domain.password.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record PasswordEntryDeleteFilterDto(
        String serviceName,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) {
}
