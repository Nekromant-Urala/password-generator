package ru.matthew.NauJava.domain.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.audit.dto.AuditCreateDto;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;
import ru.matthew.NauJava.domain.audit.exception.NotFoundAuditEventException;
import ru.matthew.NauJava.domain.audit.mapper.AuditMapper;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.user.UserRepository;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditMapper auditMapper;
    private final AuditRepository auditRepository;
    private final UserRepository userRepository;
    private final PasswordEntryRepository passwordEntryRepository;

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
        var user = userRepository.findById(dto.userId()).get();
        event.setUser(user);

        auditRepository.save(event);
        return auditMapper.toAuditResponseDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditResponseDto findById(Long id) {
        return auditRepository.findById(id)
                .map(auditMapper::toAuditResponseDto)
                .orElseThrow(
                        () -> new NotFoundAuditEventException("Не удалось найти события с таким id:%d".formatted(id))
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
    public Page<AuditResponseDto> findByUserAgent(String userAgent, Pageable pageable) {
        throw new UnsupportedOperationException("findByUserAgent: UnsupportedOperationException");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditResponseDto> findByCreatedAt(LocalDateTime createdAt, Pageable pageable) {
        return auditRepository.findAllByCreatedAt(createdAt, pageable)
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
    public long countAll() {
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
        throw new UnsupportedOperationException("countAllUserForLastDay: UnsupportedOperationException");
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllPasswordEntry() {
        return passwordEntryRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        auditRepository.deleteById(id);
    }

    @Override
    public void deleteByUserId(Long userId) {
        auditRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteByEventType(EventType event) {
        auditRepository.deleteAllByEventType(event);
    }

    @Override
    public void deleteByCreatedAt(LocalDateTime createdAt) {
        auditRepository.deleteAllByCreatedAt(createdAt);
    }

    @Override
    public void deleteByUserAgent(String userAgent) {
        throw new UnsupportedOperationException("deleteByUserAgent: UnsupportedOperationException");
    }
}
