package ru.matthew.NauJava.domain.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionService;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;
import ru.matthew.NauJava.domain.crypto.generation.RandomGeneratorService;
import ru.matthew.NauJava.domain.password.dto.PasswordResponseDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryRequestDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryUpdateDto;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryDecodeException;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.domain.password.mapper.PasswordEntryMapper;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.profile.mapper.ProfileMapper;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.nio.charset.CharacterCodingException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static ru.matthew.NauJava.common.utils.ConverterUtils.*;
import static ru.matthew.NauJava.domain.audit.EventType.*;

@Service
@Transactional
public class PasswordEntryServiceImpl implements PasswordEntryService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final EncryptionService encryptionService;
    private final RandomGeneratorService generatorService;

    private final ProfileMapper profileMapper;
    private final PasswordEntryMapper passwordEntryMapper;
    private final PasswordEntryRepository passwordEntryRepository;

    private final ApplicationEventPublisher eventPublisher;

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordEntryServiceImpl.class);

    @Autowired
    public PasswordEntryServiceImpl(
            PasswordEntryMapper passwordEntryMapper,
            UserRepository userRepository,
            PasswordEntryRepository passwordEntryRepository,
            EncryptionService encryptionService,
            ProfileRepository profileRepository, RandomGeneratorService generatorService, ProfileMapper profileMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.passwordEntryMapper = passwordEntryMapper;
        this.userRepository = userRepository;
        this.generatorService = generatorService;
        this.profileMapper = profileMapper;
        this.eventPublisher = eventPublisher;
        this.passwordEntryRepository = passwordEntryRepository;
        this.encryptionService = encryptionService;
        this.profileRepository = profileRepository;
    }

    @Override
    public PasswordEntryResponseDto createPasswordEntry(Long userId, PasswordEntryRequestDto dto) {
        var entry = passwordEntryMapper.toPasswordEntry(dto);
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );

        var profile = profileRepository.findByUserIdAndName(userId, dto.profileName())
                .or(() -> profileRepository.findByUserIdAndIsFavoriteTrue(userId))
                .orElseThrow(
                        () -> new ProfileNotFoundException("Не удалось подобрать необходимый профайл для создания пароля")
                );

        entry.setUser(user);
        entry.setProfile(profile);
        user.addPasswordEntries(entry);

        char[] password = dto.password();
        if (password == null || password.length == 0) {
            password = generatorService.generatePassword(profileMapper.toProfileForPasswordDto(profile));
        }

        try {
            entry.setPassword(bytesToString(
                    encryptionService.encrypt(
                            charsToBytes(password),
                            user.getPassword().toCharArray(),
                            profile.getCipher(),
                            profile.getKdfAlgorithm()
                    )
            ));

            entry = passwordEntryRepository.save(entry);

            eventPublisher.publishEvent(new AuditEventDto(userId, CREATE_ENTRY, "Создание записи данных"));
            LOGGER.info("Создание записи данных о пароле c id {} пользователем с id: {}", entry.getId(), userId);

            return passwordEntryMapper.toPasswordEntryResponseDto(entry);
        } catch (CharacterCodingException e) {
            throw new PasswordEntryDecodeException(e);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordEntryResponseDto findById(Long id) {
        return passwordEntryRepository.findById(id).map(passwordEntryMapper::toPasswordEntryResponseDto).orElseThrow(
                () -> new PasswordEntryNotFoundException(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PasswordEntryResponseDto> findByServiceName(Long userId, String serviceName, Pageable pageable) {
        return passwordEntryRepository.findAllByUserIdAndServiceName(userId, serviceName, pageable)
                .map(passwordEntryMapper::toPasswordEntryResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PasswordEntryResponseDto> findByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return passwordEntryRepository.findAllByUserIdAndCreatedAtBetween(userId, startDate, endDate, pageable)
                .map(passwordEntryMapper::toPasswordEntryResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PasswordEntryResponseDto> findByCreatedAt(Long userId, LocalDateTime createdAt, Pageable pageable) {
        return passwordEntryRepository.findByUserIdAndCreatedAt(userId, createdAt, pageable)
                .map(passwordEntryMapper::toPasswordEntryResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PasswordEntryResponseDto> findByUpdatedAt(Long userId, LocalDateTime updatedAt, Pageable pageable) {
        return passwordEntryRepository.findByUserIdAndUpdatedAt(userId, updatedAt, pageable)
                .map(passwordEntryMapper::toPasswordEntryResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PasswordEntryResponseDto> findAllByPageForUser(Long userId, Pageable pageable) {
        return passwordEntryRepository.findAllByUserId(userId, pageable)
                .map(passwordEntryMapper::toPasswordEntryResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordEntryResponseDto> findAll() {
        return passwordEntryRepository.findAll().stream()
                .map(passwordEntryMapper::toPasswordEntryResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllEntryByUserId(Long userId) {
        return passwordEntryRepository.countAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordResponseDto revealPassword(Long id) {
        var entry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException(id)
        );
        try {
            var profile = entry.getProfile();
            LOGGER.info("Запрос пользователя с id:{} на получение пароля из записи с id: {}", entry.getUser().getId(), entry.getId());
            return new PasswordResponseDto(bytesToChars(
                    encryptionService.decrypt(
                            charsToBytes(entry.getPassword().toCharArray()),
                            entry.getUser().getPassword().toCharArray(),
                            profile.getCipher(),
                            profile.getKdfAlgorithm()
                    )
            ));
        } catch (EncryptionException e) {
            throw new PasswordEntryDecodeException(e);
        }
    }

    @Override
    public PasswordEntryResponseDto updatePatchEntry(Long id, PasswordEntryUpdateDto dto) {
        var oldEntry = passwordEntryRepository.findById(id).orElseThrow(
                () -> new PasswordEntryNotFoundException(id)
        );

        var newEntry = passwordEntryMapper.toPasswordEntry(oldEntry, dto);
        var profile = newEntry.getProfile();

        try {
            if (dto.password() != null && dto.password().length > 0) {
                newEntry.setPassword(bytesToString(
                        encryptionService.encrypt(
                                charsToBytes(dto.password()),
                                newEntry.getUser().getPassword().toCharArray(),
                                profile.getCipher(),
                                profile.getKdfAlgorithm()
                        )));
            } else {
                newEntry.setPassword(oldEntry.getPassword());
            }

            eventPublisher.publishEvent(new AuditEventDto(newEntry.getUser().getId(), UPDATE_ENTRY, "обновление данных записи"));
            LOGGER.info("Успешное обновление данных записи c id: {} пользователем с id:{}", newEntry.getId(), newEntry.getUser().getId());

            return passwordEntryMapper.toPasswordEntryResponseDto(newEntry);
        } catch (CharacterCodingException e) {
            throw new PasswordEntryDecodeException(e);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        passwordEntryRepository.deleteByUserId(userId);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_ENTRY, "удаление всех записей пользователя"));
        LOGGER.info("Удаление всех записей пользователя с id:{}", userId);
    }

    @Override
    public void deleteByServiceName(Long userId, String serviceName) {
        passwordEntryRepository.deleteAllByUserIdAndServiceName(userId, serviceName);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_ENTRY, "удаление всех записей по указанному сервису"));
        LOGGER.info("Удаление всех записей пользователя с id: {} с заданным наименованием сервиса", userId);
    }

    @Override
    public void deleteByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        passwordEntryRepository.deleteAllByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_ENTRY, "удаление всех записей в заданных временных рамках"));
        LOGGER.info("Удаление всех записей пользователя с id: {} во временном промежутке: с {} до {}", userId, startDate, endDate);
    }

    @Override
    public void deleteById(Long userId, Long id) {
        passwordEntryRepository.deleteById(id);
        eventPublisher.publishEvent(new AuditEventDto(userId, DELETE_ENTRY, "удаление конкретной записи"));
        LOGGER.info("Удаление записи с id:{} пользователем с id:{}", id, userId);
    }
}
