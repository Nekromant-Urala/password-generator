package ru.matthew.NauJava.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.domain.report.dto.ReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Получение списка отчетов по их статусу.
     *
     * @param status статус отчета для фильтрации {@link ReportStatus}.
     * @return Список отчетов {@link Report} с указанным статусом.
     */
    List<Report> findByStatus(ReportStatus status);

    /**
     * Получение списка отчетов по дате создания.
     *
     * @param createdAt дата и время создания.
     * @return Список отчетов {@link ReportDto}, созданных в указанное время.
     */
    List<Report> findByCreatedAt(LocalDateTime createdAt);


    void deleteByCreatedAt(LocalDateTime createdAt);

    /**
     * Удаляет записи паролей, созданные в заданном временном диапазоне.
     *
     * @param createdAtAfter начальная дата диапазона.
     * @param createdAtBefore конечная дата диапазона.
     */
    void deleteByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    /**
     * Удаление всех отчетов с определенным статусом.
     *
     * @param status статус отчета для удаления.
     */
    void deleteByStatus(ReportStatus status);
}
