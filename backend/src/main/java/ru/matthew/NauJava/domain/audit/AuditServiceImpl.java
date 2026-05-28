package ru.matthew.NauJava.domain.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.matthew.NauJava.domain.audit.dto.AuditCreateDto;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;
import ru.matthew.NauJava.domain.audit.dto.AuditStatsResponseDto;
import ru.matthew.NauJava.domain.audit.exception.AuditEventNotFoundException;
import ru.matthew.NauJava.domain.audit.mapper.AuditMapper;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditMapper auditMapper;
    private final AuditRepository auditRepository;
    private final UserRepository userRepository;
    private final PasswordEntryRepository passwordEntryRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    public AuditServiceImpl(AuditMapper auditMapper, AuditRepository auditRepository, UserRepository userRepository, PasswordEntryRepository passwordEntryRepository) {
        this.auditMapper = auditMapper;
        this.auditRepository = auditRepository;
        this.userRepository = userRepository;
        this.passwordEntryRepository = passwordEntryRepository;
    }

    @Override
    public AuditResponseDto createEvent(AuditCreateDto dto) {
        var event = auditMapper.toAudit(dto);
        var user = userRepository.findById(dto.userId()).orElseThrow(
                () -> new UserNotFoundException(dto.userId())
        );
        event.setUser(user);

        auditRepository.save(event);
        LOGGER.debug("Событие для аудита сохранено. Пользователь с id:{}, тип события: {}", user.getId(), event.getEventType());
        return auditMapper.toAuditResponseDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditResponseDto findById(Long id) {
        return auditRepository.findById(id)
                .map(auditMapper::toAuditResponseDto)
                .orElseThrow(
                        () -> new AuditEventNotFoundException("Не удалось найти события с таким id:%d".formatted(id))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponseDto> findByEventType(EventType event, Pageable pageable) {
        return auditRepository.findAllByEventType(event, pageable)
                .map(auditMapper::toAuditResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponseDto> findByUserAgent(Long userId, String userAgent, Pageable pageable) {
        return auditRepository.findAllByUserIdAndUserAgent(userId, userAgent, pageable)
                .map(auditMapper::toAuditResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponseDto> findByCreatedAt(LocalDateTime createdAt, Pageable pageable) {
        return auditRepository.findByCreatedAt(createdAt, pageable)
                .map(auditMapper::toAuditResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponseDto> findByUserId(Long userId, Pageable pageable) {
        return auditRepository.findAllByUserId(userId, pageable)
                .map(auditMapper::toAuditResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponseDto> findAll(Pageable pageable) {
        return auditRepository.findAll(pageable)
                .map(auditMapper::toAuditResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllEvent() {
        return auditRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllUser() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllUserForLastDay() {
        return 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllPasswordEntries() {
        return passwordEntryRepository.count();
    }

    @Override
    public AuditStatsResponseDto getAllStatsSystem() {
        return auditMapper.toAuditStatsResponseDto(
                countAllEvent(),
                countAllUser(),
                countAllPasswordEntries(),
                countAllUserForLastDay()
        );
    }

    @Override
    public void deleteById(Long id) {
        auditRepository.deleteById(id);
        LOGGER.info("Удаление события аудита c id:{}", id);
    }

    @Override
    public void deleteByUserId(Long userId) {
        auditRepository.deleteAllByUserId(userId);
        LOGGER.info("Удаление всей истории аудита для пользователя c id:{}", userId);
    }

    @Override
    public void deleteByEventType(EventType event) {
        auditRepository.deleteAllByEventType(event);
        LOGGER.info("Удаление всей истории аудита c типом события:{}", event);
    }

    @Override
    public void deleteByCreatedAt(LocalDateTime createdAt) {
        auditRepository.deleteByCreatedAt(createdAt);
        LOGGER.info("Удаление всей истории аудита c датой создания события:{}", createdAt);
    }
}
