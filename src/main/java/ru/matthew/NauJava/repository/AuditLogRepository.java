package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matthew.NauJava.entity.AuditLog;

@RepositoryRestResource(path = "audit")
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
