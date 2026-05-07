package ru.matthew.NauJava.domain.profile.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.profile.GeneratorProfile;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileCreateDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileResponseDto;

@Component
public class GeneratorProfileMapper {

    public GeneratorProfile toGeneratorProfile(GeneratorProfileCreateDto dto) {
        if (dto == null) {
            return null;
        }
        GeneratorProfile gp = new GeneratorProfile();
        gp.setName(dto.name());
        gp.setPasswordLength(dto.passwordLength());
        gp.setUppercase(dto.isUppercase());
        gp.setLowercase(dto.isLowercase());
        gp.setDigits(dto.isDigits());
        gp.setSpecialChars(dto.isSpecialChars());
        gp.setAvoidAmbiguousChars(dto.isAvoidAmbiguousChars());
        gp.setFavorite(dto.isFavorite());
        gp.setCustomChars(dto.customChars());
        gp.setUser(dto.user());
        gp.setKdfAlgorithm(dto.kdfAlgorithm());
        gp.setCipher(dto.cipher());
        return gp;
    }



    public GeneratorProfileResponseDto toGeneratorProfileResponseDto(GeneratorProfile gp) {
        if (gp == null) {
            return null;
        }
        return new GeneratorProfileResponseDto(
                gp.getId(),
                gp.getName(),
                gp.getPasswordLength(),
                gp.isUppercase(),
                gp.isLowercase(),
                gp.isDigits(),
                gp.isSpecialChars(),
                gp.isAvoidAmbiguousChars(),
                gp.isFavorite(),
                gp.getCustomChars(),
                gp.getCreateAt(),
                gp.getKdfAlgorithm(),
                gp.getCipher()
        );
    }
}
