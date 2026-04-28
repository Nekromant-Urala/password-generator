package ru.matthew.NauJava.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "reports")
public interface ReportRepository extends JpaRepository<Report, Long> {
}
