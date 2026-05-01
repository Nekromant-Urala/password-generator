package ru.matthew.NauJava.domain.password;

import ru.matthew.NauJava.domain.password.dto.PasswordEntryCreateDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;

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
     * @param id уникальный идентификатор обновляемой записи
     * @param login новый логин (имя пользователя) для сервиса
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    PasswordEntryResponseDto updateLogin(Long id, String login);

    /**
     * Обновляет название сервиса в существующей записи пароля.
     *
     * @param id уникальный идентификатор обновляемой записи
     * @param serviceName новое название сервиса
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    PasswordEntryResponseDto updateServiceName(Long id, String serviceName);

    /**
     * Обновляет текстовое описание (заметку) в существующей записи пароля.
     *
     * @param id уникальный идентификатор обновляемой записи
     * @param description новое описание или заметка
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    PasswordEntryResponseDto updateDescription(Long id, String description);

    /**
     * Обновляет сам пароль в существующей записи.
     *
     * @param id уникальный идентификатор обновляемой записи
     * @param password новый пароль в виде массива символов (char[]) в целях безопасности
     * @return Возвращает объект {@link PasswordEntryResponseDto} с данными измененного пользователя.
     * @throws {@link PasswordEntryNotFoundException} если запись с заданным id не существует.
     */
    PasswordEntryResponseDto updatePassword(Long id, char[] password);

    /**
     * Удаляет запись пароля по названию сервиса.
     *
     * @param serviceName название сервиса, запись для которого необходимо удалить
     */
    void deleteByServiceName(String serviceName);

    /**
     * Удаляет запись пароля по её уникальному идентификатору.
     *
     * @param id уникальный идентификатор записи, которую необходимо удалить
     */
    void deleteById(Long id);
}
