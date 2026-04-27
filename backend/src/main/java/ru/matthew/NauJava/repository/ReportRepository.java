package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matthew.NauJava.entity.Report;

@RepositoryRestResource(path = "reports")
public interface ReportRepository extends JpaRepository<Report, Long> {
}
