package ru.matthew.NauJava.domain.profile.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpecConverter;
import ru.matthew.NauJava.domain.profile.GeneratorProfile;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileForPasswordDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileRequestDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileResponseDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileUpdateDto;

@Component
public class GeneratorProfileMapper {

    public GeneratorProfile toGeneratorProfile(GeneratorProfileRequestDto dto) {
        if (dto == null) {
            return null;
        }

        var cipherAlgorithmSpec = CipherAlgorithmSpec.valueOf(dto.cipher());
        var kdfAlgorithmSpec = new KdfAlgorithmSpecConverter().convertToEntityAttribute(dto.kdfAlgorithm());

        return new GeneratorProfile.GeneratorProfileBuilder()
                .name(dto.name())
                .passwordLength(dto.passwordLength())
                .uppercase(dto.isUppercase())
                .lowercase(dto.isLowercase())
                .digits(dto.isDigits())
                .specialChars(dto.isSpecialChars())
                .avoidAmbiguousChars(dto.isDuplicateChars())
                .favorite(dto.isFavorite())
                .customChars(dto.customChars())
                .kdfAlgorithm(kdfAlgorithmSpec)
                .cipher(cipherAlgorithmSpec)
                .iterations(dto.iterations())
                .build();
    }

    public void updateGeneratorProfileDto(GeneratorProfile profile, GeneratorProfileUpdateDto dto) {
        if (dto == null || profile == null) {
            return;
        }

        var cipherAlgorithmSpec = CipherAlgorithmSpec.valueOf(dto.cipher());
        var kdfAlgorithmSpec = new KdfAlgorithmSpecConverter().convertToEntityAttribute(dto.kdfAlgorithm());

        profile.setName(dto.name());
        profile.setPasswordLength(dto.passwordLength());
        profile.setUppercase(dto.isUppercase());
        profile.setLowercase(dto.isLowercase());
        profile.setDigits(dto.isDigits());
        profile.setSpecialChars(dto.isSpecialChars());
        profile.setDuplicateChars(dto.isDuplicateChars());
        profile.setFavorite(dto.isFavorite());
        profile.setCustomChars(dto.customChars());
        profile.setKdfAlgorithm(kdfAlgorithmSpec);
        profile.setCipher(cipherAlgorithmSpec);
        profile.setIterations(dto.iterations());
    }

    public GeneratorProfileForPasswordDto toProfileForPasswordDto(GeneratorProfile profile) {
        if (profile == null) {
            return null;
        }
        return new GeneratorProfileForPasswordDto(
                profile.getPasswordLength(),
                profile.isUppercase(),
                profile.isLowercase(),
                profile.isDigits(),
                profile.isSpecialChars(),
                profile.isDuplicateChars(),
                profile.getCustomChars()
        );
    }

    public GeneratorProfileResponseDto toGeneratorProfileResponseDto(GeneratorProfile profile) {
        if (profile == null) {
            return null;
        }
        return new GeneratorProfileResponseDto(
                profile.getId(),
                profile.getName(),
                profile.getPasswordLength(),
                profile.isUppercase(),
                profile.isLowercase(),
                profile.isDigits(),
                profile.isSpecialChars(),
                profile.isDuplicateChars(),
                profile.isFavorite(),
                profile.getCustomChars(),
                profile.getCreateAt(),
                profile.getKdfAlgorithm(),
                profile.getCipher(),
                profile.getIterations()
        );
    }
}
