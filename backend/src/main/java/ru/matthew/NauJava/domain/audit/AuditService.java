package ru.matthew.NauJava.domain.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.matthew.NauJava.domain.audit.dto.AuditCreateDto;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;
import ru.matthew.NauJava.domain.audit.dto.AuditStatsResponseDto;

import java.time.LocalDateTime;

/**
 * Интерфейс сервиса для управления записями аудита (логирования действий в системе).
 */
public interface AuditService {

    /**
     * Создает новую пустую или базовую запись аудита.
     *
     * @param dto информация о событии типа {@link AuditCreateDto}
     * @return DTO созданной записи аудита {@link AuditResponseDto}.
     */
    AuditResponseDto createEvent(AuditCreateDto dto);

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
     * @param event тип события аудита {@link EventType}.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    Page<AuditResponseDto> findByEventType(EventType event, Pageable pageable);

    /**
     * Поиск записей аудита по User-Agent клиента.
     *
     * @param userAgent строка User-Agent из HTTP-запроса.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    Page<AuditResponseDto> findByUserAgent(String userAgent, Pageable pageable);

    /**
     * Поиск записей аудита по дате их создания.
     *
     * @param createdAt дата и время создания записи.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    Page<AuditResponseDto> findByCreatedAt(LocalDateTime createdAt, Pageable pageable);

    /**
     * Поиск записей аудита, связанных с определенным пользователем.
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    Page<AuditResponseDto> findByUserId(Long userId, Pageable pageable);

    /**
     * Возвращает все случившиеся события в системе
     *
     * @return Возвращает все случившиеся события в системе
     */
    Page<AuditResponseDto> findAll(Pageable pageable);

    /**
     * Подсчитывает количество всех событий в системе
     *
     * @return количество всех событий в системе
     */
    long countAllEvent();

    /**
     * Подсчитывает количество всех пользователей в системе
     *
     * @return количество всех пользователей системы
     */
    long countAllUser();

    /**
     * Подсчитывает количество пользователей за последние 24 часа
     *
     * @return количество всех пользователей системы зарегистрированных за последние 24 часа
     */
    long countAllUserForLastDay();

    /**
     * Подсчитывает количество сгенерированных записей/паролей за всё время
     *
     * @return количество сгенерированных записей за все время
     */
    long countAllPasswordEntries();

    /**
     * Производит подсчет всех данных системы.
     * (Количество пользователей, сгенерированных-записей, пользователей за последний день, количество произошедших событий)
     *
     * @return Возвращает данные о системе в качестве {@link AuditStatsResponseDto}
     */
    AuditStatsResponseDto getAllStatsSystem();

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
     * @param event тип события {@link EventType}.
     */
    void deleteByEventType(EventType event);

    /**
     * Удаление записей аудита по дате их создания.
     *
     * @param createdAt дата и время создания.
     */
    void deleteByCreatedAt(LocalDateTime createdAt);
}
