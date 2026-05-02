package ru.matthew.NauJava.domain.user.dto;

public record UserForPasswordEntryDto(
        Long id,
        String username,
        String email
) {}
