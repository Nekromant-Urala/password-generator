package ru.matthew.NauJava.domain.audit;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.matthew.NauJava.domain.audit.dto.AuditDeleteFilterDto;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;
import ru.matthew.NauJava.domain.audit.dto.AuditSearchFilterDto;
import ru.matthew.NauJava.domain.audit.dto.AuditStatsResponseDto;

@RestController
@RequestMapping("/audits")
public class AuditRestController {

    private final AuditService auditService;

    @Autowired
    public AuditRestController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditResponseDto> getEventById(@PathVariable(name = "id") Long eventId) {
        var event = auditService.findById(eventId);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<AuditResponseDto>> getEvents(
            @Valid AuditSearchFilterDto filter,
            Pageable pageable
    ) {
        if (filter.userAgent() != null && filter.userId() != null) {
            var events = auditService.findByUserAgent(filter.userId(), filter.userAgent(), pageable);
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        if (filter.userId() != null) {
            var events = auditService.findByUserId(filter.userId(), pageable);
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        if (filter.eventType() != null) {
            var events = auditService.findByEventType(filter.eventType(), pageable);
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        if (filter.createdAt() != null) {
            var events = auditService.findByCreatedAt(filter.createdAt(), pageable);
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        var events = auditService.findAll(pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<AuditStatsResponseDto> getStats() {
        var stats = auditService.getAllStatsSystem();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEvents(
            @Valid AuditDeleteFilterDto filer
    ) {
        if (filer.createdAt() != null) {
            auditService.deleteByCreatedAt(filer.createdAt());
        } else if (filer.eventType() != null) {
            auditService.deleteByEventType(filer.eventType());
        } else {
            auditService.deleteByUserId(filer.userId());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable(name = "id") Long eventId) {
        auditService.deleteById(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
