package ru.matthew.NauJava.crypto.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ru.matthew.NauJava.domain.crypto.generation.RandomGeneratorService;
import ru.matthew.NauJava.domain.crypto.generation.RandomGeneratorServiceImpl;
import ru.matthew.NauJava.domain.profile.dto.ProfileForPasswordDto;


import static org.junit.jupiter.api.Assertions.assertThrows;

public class RandomGeneratorServiceTest {

    private final RandomGeneratorService randomGeneratorService = new RandomGeneratorServiceImpl();

    @Test
    public void generatePassword_WhenPasswordLengthLessPermittedLength_ThrowIllegalArgumentException() {
        var dto = new ProfileForPasswordDto(0, true, true, true, true, true, "");

        assertThrows(IllegalArgumentException.class, () -> randomGeneratorService.generatePassword(dto));
    }

    @RepeatedTest(5)
    public void generatePassword_WhenSelectOnlyDigits_ReturnGeneratedPassword() {
        int passLen = 10;
        var dto = new ProfileForPasswordDto(passLen, false, false, true, false, false, "");

        var generatedPassword = new String(randomGeneratorService.generatePassword(dto));

        Assertions.assertThat(generatedPassword).hasSize(passLen);
        Assertions.assertThat(generatedPassword).containsOnlyDigits();
    }

    @RepeatedTest(5)
    public void generatePassword_WhenSelectOnlyDigitsWithDuplicate_ReturnGeneratedPassword() {
        int passLen = 14;
        var dto = new ProfileForPasswordDto(passLen, false, false, true, false, true, "");

        var generatedPassword = new String(randomGeneratorService.generatePassword(dto));

        Assertions.assertThat(generatedPassword).hasSize(passLen);
        Assertions.assertThat(generatedPassword).containsOnlyDigits();
    }

    @RepeatedTest(5)
    public void generatePassword_WhenSelectOnlyUppercase_ReturnGeneratedPassword() {
        int passLen = 10;
        var dto = new ProfileForPasswordDto(passLen, true, false, false, false, false, "");

        var generatedPassword = new String(randomGeneratorService.generatePassword(dto));

        Assertions.assertThat(generatedPassword).hasSize(passLen);
        Assertions.assertThat(generatedPassword).matches("[A-Z]{10}");
    }

    @RepeatedTest(5)
    public void generatePassword_WhenSelectOnlyLowercase_ReturnGeneratedPassword() {
        int passLen = 10;
        var dto = new ProfileForPasswordDto(passLen, false, true, false, false, false, "");

        var generatedPassword = new String(randomGeneratorService.generatePassword(dto));

        Assertions.assertThat(generatedPassword).hasSize(passLen);
        Assertions.assertThat(generatedPassword).matches("[a-z]{10}");
    }

    @RepeatedTest(5)
    public void generatePassword_WhenSelectOnlySpecialChars_ReturnGeneratedPassword() {
        int passLen = 20;
        var dto = new ProfileForPasswordDto(passLen, false, false, false, true, true, "");

        var generatedPassword = new String(randomGeneratorService.generatePassword(dto));

        Assertions.assertThat(generatedPassword).hasSize(passLen);
        Assertions.assertThat(generatedPassword).matches("[!@#$%^&*()\\-_=+\\[{\\]}]{20}");
    }

    @RepeatedTest(5)
    public void generatePassword_WhenAllTypesSelected_ReturnGeneratedPassword() {
        int passLen = 20;
        var dto = new ProfileForPasswordDto(passLen, true, true, true, true, true, "");

        var generatedPassword = new String(randomGeneratorService.generatePassword(dto));

        Assertions.assertThat(generatedPassword).hasSize(passLen);
        Assertions.assertThat(generatedPassword)
                .containsPattern("[A-Z]+")
                .containsPattern("[a-z]+")
                .containsPattern("[0-9]+")
                .containsPattern("[!@#$%^&*()\\-_=+\\[{\\]}]+");
        Assertions.assertThat(generatedPassword).matches("[A-Za-z0-9!@#$%^&*()\\-_=+\\[{\\]}]{20}");
    }

    @Test
    public void generatePassword_WhenLengthLessThanRequiredTypes_ThrowIllegalArgumentException() {
        int passLen = 3;
        var dto = new ProfileForPasswordDto(passLen, true, true, true, true, true, "");

        assertThrows(IllegalArgumentException.class, () -> randomGeneratorService.generatePassword(dto));
    }

    @Test
    public void generatePassword_WhenNoDuplicatesAllowedAndPoolTooSmall_ThrowIllegalArgumentException() {
        int passLen = 30;
        var dto = new ProfileForPasswordDto(passLen, false, false, false, true, false, "");

        assertThrows(IllegalArgumentException.class, () -> randomGeneratorService.generatePassword(dto));
    }
}
