package ru.matthew.NauJava.domain.profile.dto;


import jakarta.validation.constraints.*;

public record ProfileRequestDto(
        @NotBlank(message = "имя профиля не может быть пустым")
        @Size(max = 100, message = "имя профиля не должно превышать 100 символов")
        String name,
        @NotNull
        @Positive
        @Max(value = 128, message = "максимальная длина пароля 128 символов")
        int passwordLength,
        Boolean isUppercase,
        Boolean isLowercase,
        Boolean isDigits,
        Boolean isSpecialChars,
        Boolean isDuplicateChars,
        Boolean isFavorite,
        @Size(max = 255, message = "список кастомных символов слишком большой")
        String customChars,
        @NotNull(message = "необходимо задавать алгоритм kdf")
        String kdfAlgorithm,
        @NotNull(message = "необходимо задать алгоритм шифрования")
        String cipher
) {
    public ProfileRequestDto {
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
