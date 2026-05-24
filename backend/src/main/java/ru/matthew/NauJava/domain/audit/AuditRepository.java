package ru.matthew.NauJava.domain.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;

import java.time.LocalDateTime;

public interface AuditRepository extends JpaRepository<Audit, Long> {

    /**
     * Поиск записей аудита по типу события.
     *
     * @param type тип события аудита {@link EventType}.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    Page<Audit> findAllByEventType(EventType type, Pageable pageable);

//    /**
//     * Поиск записей аудита по User-Agent клиента.
//     *
//     * @param userId уникальный идентификатор пользователя.
//     * @param userAgent строка User-Agent из HTTP-запроса.
//     * @return Список найденных записей {@link AuditResponseDto}.
//     */
//    Page<AuditResponseDto> findAllByUserAgent(Long userId, String userAgent, Pageable pageable);

    /**
     * Поиск записей аудита по дате их создания.
     *
     * @param createdAt дата и время создания записи.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    Page<Audit> findAllByCreatedAt(LocalDateTime createdAt, Pageable pageable);

    /**
     * Поиск записей аудита, связанных с определенным пользователем.
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Список найденных записей {@link AuditResponseDto}.
     */
    Page<Audit> findAllByUserId(Long userId, Pageable pageable);


//    /**
//     * Подсчитывает количество всех пользователей в системе
//     *
//     * @return количество всех пользователей системы
//     */
//    long countAllUser();

//    /**
//     * Подсчитывает количество пользователей за последние 24 часа
//     *
//     * @return количество всех пользователей системы зарегистрированных за последние 24 часа
//     */
//    long countAllUserForLastDay();

//    /**
//     * Подсчитывает количество сгенерированных записей/паролей за всё время
//     *
//     * @return количество сгенерированных записей за все время
//     */
//    long countAllPasswordEntry();

    /**
     * Удаление всех записей аудита, связанных с конкретным пользователем.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteAllByUserId(Long userId);

    /**
     * Удаление всех записей аудита указанного типа события.
     *
     * @param type тип события {@link EventType}.
     */
    void deleteAllByEventType(EventType type);

    /**
     * Удаление записей аудита по дате их создания.
     *
     * @param createdAt дата и время создания.
     */
    void deleteAllByCreatedAt(LocalDateTime createdAt);

    /**
     * Удаление записей аудита по-указанному User-Agent.
     *
     * @param userAgent строка User-Agent.
     */
    void deleteByUserAgent(String userAgent);
}
