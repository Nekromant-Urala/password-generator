package ru.matthew.NauJava.domain.audit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import ru.matthew.NauJava.domain.audit.EventType;

import java.time.LocalDateTime;

public record AuditDeleteFilterDto(
        @NotNull(message = "id пользователя не может быть null")
        @Positive(message = "id пользователя должно быть положительным числом")
        Long userId,
        @NotNull(message = "поле тип события не может быть null")
        EventType eventType,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAt
) {

}
