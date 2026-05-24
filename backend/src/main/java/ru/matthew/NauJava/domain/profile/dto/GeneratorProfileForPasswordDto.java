package ru.matthew.NauJava.domain.profile.dto;

public record GeneratorProfileForPasswordDto(
        Integer passwordLength,
        boolean isUppercase,
        boolean isLowercase,
        boolean isDigits,
        boolean isSpecialChars,
        boolean isDuplicateChars,
        String customChars
) {
}
