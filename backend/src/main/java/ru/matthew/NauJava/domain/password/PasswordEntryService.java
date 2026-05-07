package ru.matthew.NauJava.domain.password;

import ru.matthew.NauJava.domain.password.dto.PasswordEntryCreateDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntrySpecDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
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
     * @param dto объект передачи данных (DTO), содержащий информацию для создания записи
     * @return Возвращает объект {@link PasswordEntryCreateDto} с данными созданного пользователя.
     */
    PasswordEntryResponseDto createPasswordEntry(PasswordEntryCreateDto dto);

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
     * @param serviceName название сервиса (например, "Google", "GitHub")
     * @return Возвращает список объектов {@link PasswordEntryResponseDto} с данными найденного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    List<PasswordEntryResponseDto> findByServiceName(String serviceName);

    /**
     * Выполняет поиск записей паролей, созданных в заданном временном диапазоне.
     *
     * @param startDate начальная дата диапазона.
     * @param endDate конечная дата диапазона.
     * @return Список найденных записей {@link PasswordEntryResponseDto}.
     */
    List<PasswordEntryResponseDto> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Выполняет поиск записей паролей по точной дате создания.
     *
     * @param createdAt дата создания записей.
     * @return Список найденных записей {@link PasswordEntryResponseDto}.
     */
    List<PasswordEntryResponseDto> findByCreatedAt(LocalDateTime createdAt);

    /**
     * Выполняет поиск записей паролей по дате последнего обновления.
     *
     * @param updatedAt дата последнего обновления.
     * @return Список найденных записей {@link PasswordEntryResponseDto}.
     */
    List<PasswordEntryResponseDto> findByUpdatedAt(LocalDateTime updatedAt);

    /**
     * Выполняет поиск записи пароля, связанной с конкретным пользователем.
     *
     * @param userId DTO с данными пользователя, для которого выполняется поиск
     * @return Возвращает список объектов {@link PasswordEntryResponseDto} с данными найденного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    List<PasswordEntryResponseDto> findByUserId(Long userId);

    /**
     * Возвращает список всех существующих записей паролей.
     *
     * @return Список всех пользователей объектов {@link PasswordEntryResponseDto}
     */
    List<PasswordEntryResponseDto> findAll();

    /**
     * Обновляет логин в существующей записи пароля.
     *
     * @param userId    уникальный идентификатор обновляемой записи
     * @param login новый логин (имя пользователя) для сервиса
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным userId не существует.
     */
    PasswordEntryResponseDto updateLogin(Long userId, String login);

    /**
     * Обновляет название сервиса в существующей записи пароля.
     *
     * @param userId          уникальный идентификатор обновляемой записи
     * @param serviceName новое название сервиса
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным userId не существует.
     */
    PasswordEntryResponseDto updateServiceName(Long userId, String serviceName);

    /**
     * Обновляет текстовое описание (заметку) в существующей записи пароля.
     *
     * @param userId          уникальный идентификатор обновляемой записи
     * @param description новое описание или заметка
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным userId не существует.
     */
    PasswordEntryResponseDto updateDescription(Long userId, String description);

    /**
     * Обновляет сам пароль в существующей записи.
     *
     * @param userId  уникальный идентификатор обновляемой записи
     * @param dto данные для обновления пароля
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным userId не существует.
     */
    PasswordEntryResponseDto updatePassword(Long userId, PasswordEntrySpecDto dto);

    /**
     * Удаляет записи паролей, созданные в заданном временном диапазоне.
     *
     * @param startDate начальная дата диапазона.
     * @param endDate конечная дата диапазона.
     */
    void deleteByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Удаляет все записи паролей, принадлежащие указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteByUserId(Long userId);

    /**
     * Удаляет запись пароля по её идентификатору и названию сервиса.
     *
     * @param id уникальный идентификатор записи.
     * @param serviceName название сервиса.
     */
    void deleteByServiceName(Long id, String serviceName);

    /**
     * Удаляет запись пароля по её уникальному идентификатору.
     *
     * @param id уникальный идентификатор записи, которую необходимо удалить
     */
    void deleteById(Long id);
}
