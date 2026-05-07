package ru.matthew.NauJava.domain.audit;

import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для управления записями аудита (логирования действий в системе).
 */
public interface AuditService {

    /**
     * Создает новую пустую или базовую запись аудита.
     *
     * @return DTO созданной записи аудита {@link AuditResponseDto}.
     */
    AuditResponseDto createAudit();

    /**
     * Поиск записи аудита по уникальному идентификатору.
     *
     * @param id уникальный идентификатор записи аудита.
     * @return Найденный отчет {@link AuditResponseDto}.
     */
    AuditResponseDto findById(Long id);

    /**
     * Поиск записей аудита по типу события.
     *
     * @param type тип события аудита {@link EventType}.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    List<AuditResponseDto> findByEventType(EventType type);

    /**
     * Поиск записей аудита по User-Agent клиента.
     *
     * @param userAgent строка User-Agent из HTTP-запроса.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    List<AuditResponseDto> findByUserAgent(String userAgent);

    /**
     * Поиск записей аудита по дате их создания.
     *
     * @param createdAt дата и время создания записи.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    List<AuditResponseDto> findByCreatedAt(LocalDateTime createdAt);

    /**
     * Поиск записей аудита, связанных с определенным пользователем.
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    List<AuditResponseDto> findByUserId(Long userId);

    /**
     * Удаление записи аудита по её уникальному идентификатору.
     *
     * @param id идентификатор удаляемой записи.
     */
    void deleteById(Long id);

    /**
     * Удаление всех записей аудита, связанных с конкретным пользователем.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteByUserId(Long userId);

    /**
     * Удаление всех записей аудита указанного типа события.
     *
     * @param type тип события {@link EventType}.
     */
    void deleteByEventType(EventType type);

    /**
     * Удаление записей аудита по дате их создания.
     *
     * @param createdAt дата и время создания.
     */
    void deleteByCreatedAt(LocalDateTime createdAt);

    /**
     * Удаление записей аудита по-указанному User-Agent.
     *
     * @param userAgent строка User-Agent.
     */
    void deleteByUserAgent(String userAgent);
}
