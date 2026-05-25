package ru.matthew.NauJava.domain.profile.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ProfileDeleteFilterDto(
        String name,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createAt,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate
) {
}
