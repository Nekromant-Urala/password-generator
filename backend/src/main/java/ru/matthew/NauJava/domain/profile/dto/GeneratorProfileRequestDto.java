package ru.matthew.NauJava.domain.profile.dto;


public record GeneratorProfileRequestDto(
        String name,
        int passwordLength,
        Boolean isUppercase,
        Boolean isLowercase,
        Boolean isDigits,
        Boolean isSpecialChars,
        Boolean isDuplicateChars,
        Boolean isFavorite,
        String customChars,
        String kdfAlgorithm,
        String cipher,
        int iterations
) {
    public GeneratorProfileRequestDto {
        if (isUppercase == null) {
            isUppercase = false;
        }
        if (isLowercase == null) {
            isLowercase = false;
        }
        if (isDigits == null) {
            isDigits = false;
        }
        if (isSpecialChars == null) {
            isSpecialChars = false;
        }
        if (isDuplicateChars == null) {
            isDuplicateChars = false;
        }
        if (isFavorite == null) {
            isFavorite = false;
        }
    }
}
