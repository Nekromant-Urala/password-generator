package ru.matthew.NauJava.domain.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec;
import ru.matthew.NauJava.domain.profile.dto.ProfileRequestDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileResponseDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileUpdateDto;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.profile.mapper.ProfileMapper;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.matthew.NauJava.domain.audit.EventType.*;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    private final ProfileMapper profileMapper;
    private final ProfileRepository profileRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ProfileServiceImpl(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            ProfileMapper profileMapper, ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public ProfileResponseDto createProfile(Long userId, ProfileRequestDto dto) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        var profile = profileMapper.toGeneratorProfile(dto);

        profile.setUser(user);
        user.addProfile(profile);
        profileRepository.save(profile);

        eventPublisher.publishEvent(new AuditEventDto(userId, CREATE_PROFILE, "создание профиля-генерации"));

        return profileMapper.toGeneratorProfileResponseDto(profile);
    }

    @Override
    public ProfileResponseDto createDefaultProfile(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        var defaultProfile = new Profile.GeneratorProfileBuilder()
                .name("default")
                .passwordLength(12)
                .uppercase(true)
                .lowercase(true)
                .digits(true)
                .specialChars(true)
                .avoidAmbiguousChars(false)
                .favorite(true)
                .customChars("")
                .kdfAlgorithm(Pbkdf2Spec.PBKDF_2)
                .cipher(CipherAlgorithmSpec.AES)
                .iterations(1000)
                .build();

        defaultProfile.setUser(user);
        user.addProfile(defaultProfile);

        profileRepository.save(defaultProfile);
        return profileMapper.toGeneratorProfileResponseDto(defaultProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto findById(Long id) {
        return profileRepository.findById(id)
                .map(profileMapper::toGeneratorProfileResponseDto)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Профайл генерации не был найден по id:%d".formatted(id))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto findByName(Long userId, String name) {
        return profileRepository.findByUserIdAndName(userId, name)
                .map(profileMapper::toGeneratorProfileResponseDto)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Профайл генерации c именем:%s не был найден".formatted(name))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> findAllByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return profileRepository.findAllByUserIdAndCreateAtBetween(userId, startDate, endDate, pageable)
                .map(profileMapper::toGeneratorProfileResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> findAllByCreateAt(Long userId, LocalDateTime createAt, Pageable pageable) {
        return profileRepository.findAllByUserIdAndCreateAt(userId, createAt, pageable)
                .map(profileMapper::toGeneratorProfileResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        return profileRepository.findAllByUserId(userId, pageable)
                .map(profileMapper::toGeneratorProfileResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponseDto> findAll() {
        return profileRepository.findAll().stream()
                .map(profileMapper::toGeneratorProfileResponseDto)
                .toList();
    }

    @Override
    public long countAllProfilesByUserId(Long userId) {
        return profileRepository.countAllByUserId(userId);
    }

    @Override
    //TODO добавить логику перешифрования данных, если изменились критичные параметры
    public ProfileResponseDto updateSettings(Long id, ProfileUpdateDto dto) {
        var profile = profileRepository.findById(id).orElseThrow(
                () -> new ProfileNotFoundException(id)
        );
        profileMapper.updateGeneratorProfileDto(profile, dto);

        eventPublisher.publishEvent(new AuditEventDto(profile.getUser().getId(), UPDATE_PROFILE, "обновление настроек профиля-генерации"));

        return profileMapper.toGeneratorProfileResponseDto(profile);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        profileRepository.deleteAllByUserId(userId);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление всех профилей-генерации пользователя"));
    }

    @Override
    public void deleteAllByCreatedAt(Long userId, LocalDateTime createAt) {
        profileRepository.deleteAllByUserIdAndCreateAt(userId, createAt);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление всех профилей-генерации пользователя по дате создания"));
    }

    @Override
    public void deleteAllByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        profileRepository.deleteAllByUserIdAndCreateAtBetween(userId, startDate, endDate);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление всех профилей-генерации пользователя по заданному периоду времени"));
    }

    @Override
    public void deleteByName(Long userId, String name) {
        profileRepository.deleteByUserIdAndName(userId, name);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление профиля-генерации по заданному имени"));
    }

    @Override
    public void deleteById(Long userId, Long id) {
        profileRepository.deleteById(id);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление профиля-генерации по id"));
    }
}
