package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
