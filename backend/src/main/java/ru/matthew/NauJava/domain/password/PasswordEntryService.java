package ru.matthew.NauJava.domain.password;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.matthew.NauJava.domain.password.dto.PasswordResponseDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryRequestDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryUpdateDto;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для управления записями паролей.
 */
public interface PasswordEntryService {

    /**
     * Создает новую запись пароля на основе переданных данных.
     *
     * @param userId уникальный идентификатор пользователя
     * @param dto объект передачи данных (DTO), содержащий информацию для создания записи
     * @return Возвращает объект {@link PasswordEntryRequestDto} с данными созданного пользователя.
     */
    PasswordEntryResponseDto createPasswordEntry(Long userId, PasswordEntryRequestDto dto);

    /**
     * Выполняет поиск записи пароля по её уникальному идентификатору.
     *
     * @param id уникальный идентификатор записи пароля
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными найденного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    PasswordEntryResponseDto findById(Long id);

    /**
     * Выполняет поиск записи пароля по точному названию сервиса.
     *
     * @param userId уникальный идентификатор пользователя
     * @param serviceName название сервиса (например, "Google", "GitHub")
     * @return Возвращает список объектов {@link PasswordEntryResponseDto} с данными найденного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    Page<PasswordEntryResponseDto> findByServiceName(Long userId, String serviceName, Pageable pageable);

    /**
     * Выполняет поиск записей паролей, созданных в заданном временном диапазоне.
     *
     * @param userId уникальный идентификатор пользователя
     * @param startDate начальная дата диапазона.
     * @param endDate конечная дата диапазона.
     * @return Список найденных записей {@link PasswordEntryResponseDto}.
     */
    Page<PasswordEntryResponseDto> findByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Выполняет поиск записей паролей по точной дате создания.
     *
     * @param userId уникальный идентификатор пользователя
     * @param createdAt дата создания записей.
     * @return Список найденных записей {@link PasswordEntryResponseDto}.
     */
    Page<PasswordEntryResponseDto> findByCreatedAt(Long userId, LocalDateTime createdAt, Pageable pageable);

    /**
     * Выполняет поиск записей паролей по дате последнего обновления.
     *
     * @param userId уникальный идентификатор пользователя
     * @param updatedAt дата последнего обновления.
     * @return Список найденных записей {@link PasswordEntryResponseDto}.
     */
    Page<PasswordEntryResponseDto> findByUpdatedAt(Long userId, LocalDateTime updatedAt, Pageable pageable);

    /**
     * Выполняет поиск всех записей, связанных с конкретным пользователем.
     *
     * @param userId DTO с данными пользователя, для которого выполняется поиск
     * @return Возвращает список объектов {@link PasswordEntryResponseDto} с данными найденного записи.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    List<PasswordEntryResponseDto> findAllForUser(Long userId);

    /**
     * Возвращает все записи (постранично) для конкретного пользователя
     *
     * @param userId уникальный идентификатор пользователя
     * @param pageable объект, содержащий информацию о номере страницы, размере и сортировке
     * @return страница с найденными записями.
     */
    Page<PasswordEntryResponseDto> findAllByPageForUser(Long userId, Pageable pageable);

    /**
     * Возвращает список всех существующих записей паролей.
     *
     * @return Список всех пользователей объектов {@link PasswordEntryResponseDto}
     */
    List<PasswordEntryResponseDto> findAll();

    /**
     * Обновляет все данные записи
     *
     * @param id уникальный идентификатор записи
     * @param dto данные для обновления пароля
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененной записи.
     */
    PasswordEntryResponseDto updatePatchEntry(Long id, PasswordEntryUpdateDto dto);

    /**
     * Подсчет количества записей конкретного пользователя
     *
     * @param userId уникальный идентификатор обновляемой пользователя
     * @return Возвращает количество записей для конкретного пользователя
     */
    long countAllEntryByUserId(Long userId);

    /**
     * Возвращает клиенту его пароль в расшифрованном виде
     *
     * @param id уникальный идентификатор записи
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными записи (открытым паролем).
     */
    PasswordResponseDto revealPassword(Long id);

    /**
     * Удаляет записи паролей, созданные в заданном временном диапазоне.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param startDate начальная дата диапазона.
     * @param endDate конечная дата диапазона.
     */
    void deleteByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Удаляет все записи паролей, принадлежащие указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteByUserId(Long userId);

    /**
     * Удаляет запись пароля по её идентификатору и названию сервиса.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param serviceName название сервиса.
     */
    void deleteByServiceName(Long userId, String serviceName);

    /**
     * Удаляет запись пароля по её уникальному идентификатору.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param id уникальный идентификатор записи, которую необходимо удалить
     */
    void deleteById(Long userId, Long id);
}
