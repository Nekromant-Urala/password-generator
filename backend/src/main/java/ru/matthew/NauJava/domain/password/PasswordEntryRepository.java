package ru.matthew.NauJava.domain.password;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

    /**
     * Выполняет поиск записи пароля по точному названию сервиса.
     *
     * @param serviceName название сервиса (например, "Google", "GitHub")
     * @return Возвращает список объектов {@link PasswordEntry} с данными найденного пользователя.
     */
    List<PasswordEntry> findByServiceName(String serviceName);


    /**
     * Выполняет поиск записей паролей, созданных в заданном временном диапазоне.
     *
     * @param createdAtAfter начальная дата диапазона.
     * @param createdAtBefore конечная дата диапазона.
     * @return Список найденных записей {@link PasswordEntry}.
     */
    List<PasswordEntry> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    /**
     * Выполняет поиск записей паролей по точной дате создания.
     *
     * @param createdAt дата создания записей.
     * @return Список найденных записей {@link PasswordEntry}.
     */
    List<PasswordEntry> findByCreatedAt(LocalDateTime createdAt);

    /**
     * Выполняет поиск записей паролей по дате последнего обновления.
     *
     * @param updatedAt дата последнего обновления.
     * @return Список найденных записей {@link PasswordEntry}.
     */
    List<PasswordEntry> findByUpdatedAt(LocalDateTime updatedAt);

    /**
     * Выполняет поиск записи пароля, связанной с конкретным пользователем.
     *
     * @param userId DTO с данными пользователя, для которого выполняется поиск
     * @return Возвращает список объектов {@link PasswordEntry} с данными найденного пользователя.
     */
    List<PasswordEntry> findByUserId(Long userId);

    /**
     * Удаляет записи паролей, созданные в заданном временном диапазоне.
     *
     * @param createdAtAfter начальная дата диапазона.
     * @param createdAtBefore конечная дата диапазона.
     */
    void deleteByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    /**
     * Удаляет все записи паролей, принадлежащие указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteByUserId(Long userId);

    /**
     * Удаляет запись пароля по её идентификатору и названию сервиса.
     *
     * @param serviceName название сервиса.
     */
    void deleteByServiceName(String serviceName);
}
