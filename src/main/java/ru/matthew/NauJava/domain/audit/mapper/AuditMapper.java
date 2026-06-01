package ru.matthew.NauJava.domain.audit.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.audit.Audit;
import ru.matthew.NauJava.domain.audit.dto.AuditCreateDto;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;
import ru.matthew.NauJava.domain.audit.dto.AuditStatsResponseDto;

@Component
public class AuditMapper {

    public Audit toAudit(AuditCreateDto dto) {
        if (dto == null) {
            return null;
        }

        var event = new Audit();
        event.setEventType(dto.eventType());
        event.setUserAgent(dto.userAgent());
        event.setDescription(dto.description());
        return event;
    }

    public AuditStatsResponseDto toAuditStatsResponseDto(
            long totalEvent,
            long totalUser,
            long totalEntries,
            long totalUsersForLastDay,
            String popularCipher
    ) {
        return new AuditStatsResponseDto(
                totalEvent,
                totalUser,
                totalEntries,
                totalUsersForLastDay,
                popularCipher
        );
    }

    public AuditResponseDto toAuditResponseDto(Audit audit) {
        if (audit == null) {
            return null;
        }

        return new AuditResponseDto(
                audit.getId(),
                audit.getUser().getId(),
                audit.getEventType(),
                audit.getDescription(),
                audit.getUserAgent(),
                audit.getCreatedAt()
        );
    }
}
