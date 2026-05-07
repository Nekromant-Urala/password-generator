package ru.matthew.NauJava.domain.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Autowired
    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public AuditResponseDto createAudit() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public AuditResponseDto findById(Long id) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditResponseDto> findByEventType(EventType type) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditResponseDto> findByUserAgent(String userAgent) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditResponseDto> findByCreatedAt(LocalDateTime createdAt) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditResponseDto> findByUserId(Long userId) {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteByUserId(Long userId) {

    }

    @Override
    public void deleteByEventType(EventType type) {

    }

    @Override
    public void deleteByCreatedAt(LocalDateTime createdAt) {

    }

    @Override
    public void deleteByUserAgent(String userAgent) {

    }
}
