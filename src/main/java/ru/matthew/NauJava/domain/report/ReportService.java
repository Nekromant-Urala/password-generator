package ru.matthew.NauJava.domain.report;


import ru.matthew.NauJava.domain.report.dto.ReportDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для управления отчетами в системе
 */
public interface ReportService {

    /**
     * Создание нового отчета
     *
     * @return DTO с данными созданного отчета {@link ReportDto}.
     */
    ReportDto createReport();

    /**
     * Поиск отчета по уникальному идентификатору.
     *
     * @param id уникальный идентификатор отчета.
     * @return DTO с найденным отчетом {@link ReportDto}.
     */
    ReportDto findById(Long id);

    /**
     * Получение списка отчетов по их статусу.
     *
     * @param status статус отчета для фильтрации {@link ReportStatus}.
     * @return Список отчетов {@link ReportDto} с указанным статусом.
     */
    List<ReportDto> findByStatus(ReportStatus status);

    /**
     * Получение списка отчетов по дате создания.
     *
     * @param createdAt дата и время создания.
     * @return Список отчетов {@link ReportDto}, созданных в указанное время.
     */
    List<ReportDto> findByCreatedAt(LocalDateTime createdAt);

    /**
     * Возвращает список всех существующих отчетов.
     *
     * @return Список всех профилей {@link ReportDto}.
     */
    List<ReportDto> findAll();

    /**
     * Удаление отчета по уникальному идентификатору.
     *
     * @param id уникальный идентификатор отчета.
     */
    void deleteById(Long id);

    /**
     * Удаление всех отчетов с определенным статусом.
     *
     * @param status статус отчета для удаления.
     */
    void deleteByStatus(ReportStatus status);

    /**
     * Удаление отчетов по дате их создания.
     *
     * @param createdAt дата и время создания удаляемых отчетов.
     */
    void deleteByCreatedAt(LocalDateTime createdAt);

    /**
     * Удаляет записи паролей, созданные в заданном временном диапазоне.
     *
     * @param startDate начальная дата диапазона.
     * @param endDate конечная дата диапазона.
     */
    void deleteByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
