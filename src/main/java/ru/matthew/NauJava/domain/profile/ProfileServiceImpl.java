package ru.matthew.NauJava.domain.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.matthew.NauJava.domain.profile.exception.ProfileAlreadyExistsException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Autowired
    public ProfileServiceImpl(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            ProfileMapper profileMapper,
            ApplicationEventPublisher eventPublisher
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
        if (profileRepository.findByUserIdAndName(userId, dto.name()).isPresent()) {
            throw new ProfileAlreadyExistsException("Профиль-генерации с таким именем уже существует");
        }

        var profile = profileMapper.toProfile(dto);

        profile.setUser(user);
        user.addProfile(profile);

        if (profile.isFavorite()) {
            profileRepository.resetFavoriteProfileForUser(userId);
        }
        profile = profileRepository.save(profile);

        eventPublisher.publishEvent(new AuditEventDto(userId, CREATE_PROFILE, "создание профиля-генерации"));
        LOGGER.info("Создание профайла-генерация пользователем с id:{}", userId);

        return profileMapper.toProfileResponseDto(profile);
    }

    @Override
    public void createDefaultProfile(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        var defaultProfile = new Profile.ProfileBuilder()
                .name("default")
                .passwordLength(12)
                .uppercase(true)
                .lowercase(true)
                .digits(true)
                .specialChars(true)
                .duplicateChars(true)
                .favorite(true)
                .customChars("")
                .kdfAlgorithm(Pbkdf2Spec.PBKDF_2)
                .cipher(CipherAlgorithmSpec.AES)
                .build();

        defaultProfile.setUser(user);
        user.addProfile(defaultProfile);
        LOGGER.info("Создание профайла по умолчанию для пользователя с id:{}", userId);

        profileRepository.save(defaultProfile);
    }

    @Override
    public void setProfileAsFavorite(Long userId, Long profileId) {
        Profile profileToActivate = profileRepository.findById(profileId)
                .filter(p -> p.getUser().getId().equals(userId))
                .orElseThrow(() -> new ProfileNotFoundException("Профиль не найден или не принадлежит пользователю"));

        if (profileToActivate.isFavorite()) {
            return;
        }

        profileRepository.resetFavoriteProfileForUser(userId);
        profileToActivate.setFavorite(true);

        profileRepository.save(profileToActivate);
        eventPublisher.publishEvent(new AuditEventDto(userId, UPDATE_PROFILE, "смена активного профиля"));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto findById(Long id) {
        return profileRepository.findById(id)
                .map(profileMapper::toProfileResponseDto)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Профайл генерации не был найден по id:%d".formatted(id))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto findByName(Long userId, String name) {
        return profileRepository.findByUserIdAndName(userId, name)
                .map(profileMapper::toProfileResponseDto)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Профайл генерации c именем:%s не был найден".formatted(name))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> findAllByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return profileRepository.findAllByUserIdAndCreateAtBetween(userId, startDate, endDate, pageable)
                .map(profileMapper::toProfileResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> findAllByCreateAt(Long userId, LocalDateTime createAt, Pageable pageable) {
        return profileRepository.findAllByUserIdAndCreateAt(userId, createAt, pageable)
                .map(profileMapper::toProfileResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        return profileRepository.findAllByUserId(userId, pageable)
                .map(profileMapper::toProfileResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponseDto> findAll() {
        return profileRepository.findAll().stream()
                .map(profileMapper::toProfileResponseDto)
                .toList();
    }

    @Override
    public long countAllProfilesByUserId(Long userId) {
        return profileRepository.countAllByUserId(userId);
    }

    @Override
    public ProfileResponseDto updateSettings(Long userId, Long id, ProfileRequestDto dto) {
        var profile = profileRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(userId))
                .orElseThrow(() -> new ProfileNotFoundException(id));

        if (dto.isFavorite()) {
            setProfileAsFavorite(userId, profile.getId());
        }
        profileMapper.updateProfileDto(profile, dto);

        eventPublisher.publishEvent(new AuditEventDto(profile.getUser().getId(), UPDATE_PROFILE, "обновление настроек профиля-генерации"));
        LOGGER.info("Успешное обновление данных профайла генерации пользователем с id:{} profile:{}", profile.getUser().getId(), profile.getId());

        return profileMapper.toProfileResponseDto(profile);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        profileRepository.deleteAllByUserId(userId);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление всех профилей-генерации пользователя"));
        LOGGER.info("Удаление всех профилей-генерации пользователя c id:{}", userId);
    }

    @Override
    public void deleteAllByCreatedAt(Long userId, LocalDateTime createAt) {
        profileRepository.deleteAllByUserIdAndCreateAt(userId, createAt);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление всех профилей-генерации пользователя по дате создания"));
        LOGGER.info("Удаление всех профилей-генерации пользователя c id:{} по заданному времени: {}", userId, createAt);
    }

    @Override
    public void deleteAllByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        profileRepository.deleteAllByUserIdAndCreateAtBetween(userId, startDate, endDate);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление всех профилей-генерации пользователя по заданному периоду времени"));
        LOGGER.info("Удаление всех профилей-генерации пользователя c id:{} по заданному диапазону времени: с {} по {}", userId, startDate, endDate);
    }

    @Override
    public void deleteByName(Long userId, String name) {
        profileRepository.deleteByUserIdAndName(userId, name);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление профиля-генерации по заданному имени"));
        LOGGER.info("Удаление профиля-генерации пользователя c id:{} по заданному имени профиля", userId);
    }

    @Override
    public void deleteById(Long userId, Long id) {
        profileRepository.deleteById(id);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_PROFILE, "удаление профиля-генерации по id"));
        LOGGER.info("Удаление профиля-генерации пользователя c id:{} по заданному id события: {}", userId, id);
    }
}
