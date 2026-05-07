package ru.matthew.NauJava.domain.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileCreateDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileResponseDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileSettingsDto;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.profile.mapper.GeneratorProfileMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class GeneratorProfileServiceImpl implements GeneratorProfileService {

    private final GeneratorProfileRepository generatorProfileRepository;
    private final GeneratorProfileMapper profileMapper;

    @Autowired
    public GeneratorProfileServiceImpl(GeneratorProfileRepository generatorProfileRepository, GeneratorProfileMapper profileMapper) {
        this.generatorProfileRepository = generatorProfileRepository;
        this.profileMapper = profileMapper;
    }

    @Override
    public GeneratorProfileResponseDto createGenerateProfile(GeneratorProfileCreateDto dto) {
        var profile = profileMapper.toGeneratorProfile(dto);
        generatorProfileRepository.save(profile);
        return profileMapper.toGeneratorProfileResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneratorProfileResponseDto findById(Long id) {
        return generatorProfileRepository.findById(id)
                .map(profileMapper::toGeneratorProfileResponseDto)
                .orElseThrow(
                        () -> new ProfileNotFoundException(id)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratorProfileResponseDto> findByName(String name) {
        return generatorProfileRepository.findByName(name).stream()
                .map(profileMapper::toGeneratorProfileResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratorProfileResponseDto> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return generatorProfileRepository.findByCreateAtBetween(startDate, endDate).stream()
                .map(profileMapper::toGeneratorProfileResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratorProfileResponseDto> findByCreateAt(LocalDateTime createAt) {
        return generatorProfileRepository.findByCreateAt(createAt).stream()
                .map(profileMapper::toGeneratorProfileResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratorProfileResponseDto> findByUserId(Long userId) {
        return generatorProfileRepository.findByUserId(userId).stream()
                .map(profileMapper::toGeneratorProfileResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratorProfileResponseDto> findAll() {
        return generatorProfileRepository.findAll().stream()
                .map(profileMapper::toGeneratorProfileResponseDto)
                .toList();
    }

    @Override
    public GeneratorProfileResponseDto updateName(Long id, String name) {
        var profile = generatorProfileRepository.findById(id).orElseThrow(
                () -> new ProfileNotFoundException(id)
        );
        profile.setName(name);
        generatorProfileRepository.save(profile);
        return profileMapper.toGeneratorProfileResponseDto(profile);
    }

    @Override //TODO добавить биледр в сущность
    public GeneratorProfileResponseDto updateSettings(Long id, GeneratorProfileSettingsDto dto) {
        var profile = generatorProfileRepository.findById(id).orElseThrow(
                () -> new ProfileNotFoundException(id)
        );
        profile.setPasswordLength(dto.passwordLength());
        profile.setUppercase(dto.isUppercase());
        profile.setLowercase(dto.isLowercase());
        profile.setDigits(dto.isDigits());
        profile.setSpecialChars(dto.isSpecialChars());
        profile.setAvoidAmbiguousChars(dto.isAvoidAmbiguousChars());
        profile.setFavorite(dto.isFavorite());
        profile.setCustomChars(dto.customChars());
        generatorProfileRepository.save(profile);
        return profileMapper.toGeneratorProfileResponseDto(profile);
    }

    @Override
    public GeneratorProfileResponseDto updateKdfAlgorithm(Long id, KdfAlgorithmSpec kdfAlgorithm) {
        var profile = generatorProfileRepository.findById(id).orElseThrow(
                () -> new ProfileNotFoundException(id)
        );
        profile.setKdfAlgorithm(kdfAlgorithm);
        generatorProfileRepository.save(profile);
        return profileMapper.toGeneratorProfileResponseDto(profile);
    }

    @Override //TODO исправить во всех методах обновления данных поиск и сохранение. И добавить сохранение связей в коллекциях сущностей
    public GeneratorProfileResponseDto updateCipher(Long id, CipherAlgorithmSpec cipherAlgorithm) {
        var profile = generatorProfileRepository.findById(id).orElseThrow(
                () -> new ProfileNotFoundException(id)
        );
        profile.setCipher(cipherAlgorithm);
        generatorProfileRepository.save(profile);
        return profileMapper.toGeneratorProfileResponseDto(profile);
    }

    @Override
    public void deleteByUserId(Long userId) {
        generatorProfileRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByCreatedAt(LocalDateTime createAt) {
        generatorProfileRepository.deleteByCreateAt(createAt);
    }

    @Override
    public void deleteByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        generatorProfileRepository.deleteByCreateAtBetween(startDate, endDate);
    }

    @Override
    public void deleteByName(String name) {
        generatorProfileRepository.deleteByName(name);
    }

    @Override
    public void deleteById(Long id) {
        generatorProfileRepository.deleteById(id);
    }
}
