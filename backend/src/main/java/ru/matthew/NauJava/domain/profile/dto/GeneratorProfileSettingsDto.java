package ru.matthew.NauJava.domain.profile.dto;


public record GeneratorProfileSettingsDto(
        Integer passwordLength,
        boolean isUppercase,
        boolean isLowercase,
        boolean isDigits,
        boolean isSpecialChars,
        boolean isAvoidAmbiguousChars,
        boolean isFavorite,
        String customChars
) {
}
