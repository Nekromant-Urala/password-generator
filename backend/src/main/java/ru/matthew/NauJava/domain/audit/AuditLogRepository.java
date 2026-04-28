package ru.matthew.NauJava.domain.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "audit")
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
