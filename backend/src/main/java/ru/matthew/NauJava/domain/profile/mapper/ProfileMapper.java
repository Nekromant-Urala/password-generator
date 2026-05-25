package ru.matthew.NauJava.domain.profile.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpecConverter;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.profile.dto.ProfileForPasswordDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileRequestDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileResponseDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileUpdateDto;

@Component
public class ProfileMapper {

    public Profile toGeneratorProfile(ProfileRequestDto dto) {
        if (dto == null) {
            return null;
        }

        var cipherAlgorithmSpec = CipherAlgorithmSpec.valueOf(dto.cipher());
        var kdfAlgorithmSpec = new KdfAlgorithmSpecConverter().convertToEntityAttribute(dto.kdfAlgorithm());

        return new Profile.GeneratorProfileBuilder()
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

    public void updateGeneratorProfileDto(Profile profile, ProfileUpdateDto dto) {
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

    public ProfileForPasswordDto toProfileForPasswordDto(Profile profile) {
        if (profile == null) {
            return null;
        }
        return new ProfileForPasswordDto(
                profile.getPasswordLength(),
                profile.isUppercase(),
                profile.isLowercase(),
                profile.isDigits(),
                profile.isSpecialChars(),
                profile.isDuplicateChars(),
                profile.getCustomChars()
        );
    }

    public ProfileResponseDto toGeneratorProfileResponseDto(Profile profile) {
        if (profile == null) {
            return null;
        }
        return new ProfileResponseDto(
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
