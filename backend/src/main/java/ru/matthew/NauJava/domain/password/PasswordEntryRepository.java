package ru.matthew.NauJava.domain.password;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

    /**
     * Выполняет поиск записи пароля по точному названию сервиса.
     *
     * @param userId уникальный идентификатор записи
     * @param serviceName название сервиса (например, "Google", "GitHub")
     * @return Возвращает список объектов {@link PasswordEntry} с данными найденного пользователя.
     */
    Page<PasswordEntry> findAllByUserIdAndServiceName(Long userId, String serviceName, Pageable pageable);


    /**
     * Выполняет поиск записей паролей, созданных в заданном временном диапазоне.
     *
     * @param userId уникальный идентификатор записи
     * @param createdAtAfter начальная дата диапазона.
     * @param createdAtBefore конечная дата диапазона.
     * @return Список найденных записей {@link PasswordEntry}.
     */
    Page<PasswordEntry> findAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, Pageable pageable);

    /**
     * Выполняет поиск записей паролей по точной дате создания.
     *
     * @param userId уникальный идентификатор записи
     * @param createdAt дата создания записей.
     * @return Список найденных записей {@link PasswordEntry}.
     */
    Page<PasswordEntry> findAllByUserIdAndCreatedAt(Long userId, LocalDateTime createdAt, Pageable pageable);

    /**
     * Выполняет поиск записей паролей по дате последнего обновления.
     *
     * @param userId уникальный идентификатор записи
     * @param updatedAt дата последнего обновления.
     * @return Список найденных записей {@link PasswordEntry}.
     */
    Page<PasswordEntry> findAllByUserIdAndUpdatedAt(Long userId, LocalDateTime updatedAt, Pageable pageable);

    /**
     * Возвращает записи постранично
     *
     * @param userId уникальный идентификатор пользователя
     * @param pageable объект, содержащий информацию о номере страницы, размере и сортировке
     * @return страница с найденными записями.
     */
    Page<PasswordEntry> findByUserId(Long userId, Pageable pageable);

    /**
     * Выполняет поиск записи пароля, связанной с конкретным пользователем.
     *
     * @param userId DTO с данными пользователя, для которого выполняется поиск
     * @return Возвращает список объектов {@link PasswordEntry} с данными найденного пользователя.
     */
    @Query("SELECT p FROM PasswordEntry p WHERE p.user.id = :userId")
    List<PasswordEntry> findByUserId(@Param("userId") Long userId);

    /**
     * Выполняет поиск записи пароля, связанной с конкретным пользователем.
     *
     * @param username DTO с данными пользователя, для которого выполняется поиск
     * @return Возвращает список объектов {@link PasswordEntry} с данными найденного пользователя.
     */
    @Query("SELECT p FROM PasswordEntry p WHERE p.user.username = :username")
    List<PasswordEntry> findByUsername(@Param("username") String username);

    /**
     * Подсчет количества записей конкретного пользователя
     *
     * @param userId уникальный идентификатор пользователя
     * @return Возвращает количество записей для конкретного пользователя
     */
    @Query("SELECT count(p) FROM PasswordEntry p WHERE p.user.id = :userId")
    long countAllByUserId(@Param("userId") Long userId);

    /**
     * Удаляет записи паролей, созданные в заданном временном диапазоне.
     *
     * @param userId уникальный идентификатор записи
     * @param createdAtAfter начальная дата диапазона.
     * @param createdAtBefore конечная дата диапазона.
     */
    void deleteAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    /**
     * Удаляет все записи паролей, принадлежащие указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteByUserId(Long userId);

    /**
     * Удаляет запись пароля по её идентификатору и названию сервиса.
     *
     * @param userId уникальный идентификатор записи
     * @param serviceName название сервиса.
     */
    void deleteAllByUserIdAndServiceName(Long userId, String serviceName);
}
