package ru.matthew.NauJava.domain.user;

import ru.matthew.NauJava.domain.user.dto.UserCreateDto;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями.
 * Обрабатывает бизнес-логику создания, обновления и поиска пользователей.
 */
public interface UserService {

    /**
     * Регистрация нового пользователя в системе.
     * Выполняет хеширование пароля и назначает роль по умолчанию.
     *
     * @param userDto объект передачи данных (DTO), содержащий информацию для создания пользователя.
     * @return Возвращает объект {@link UserResponseDto} с данными созданного пользователя.
     * @throws {@link UserAlreadyExistsException} если такой пользователь уже существует.
     */
    UserResponseDto createUser(UserCreateDto userDto);

    /**
     * Нахождение пользователя по уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return Возвращает объект {@link UserResponseDto} с данными найденного пользователя.
     * @throws {@link UserNotFoundException} если пользователь с заданным id не существует.
     */
    UserResponseDto findById(Long id);

    /**
     * Нахождение пользователя по его имени (username).
     *
     * @param username имя пользователя.
     * @return Возвращает объект {@link UserResponseDto} с данными найденного пользователя.
     * @throws {@link UserNotFoundException} если пользователь с заданным username не существует.
     */
    UserResponseDto findByUsername(String username);

    /**
     * Нахождения пользователя по почте (email).
     *
     * @param email адрес электронной почты пользователя.
     * @return Возвращает объект {@link UserResponseDto} с данными найденного пользователя.
     * @throws {@link UserNotFoundException} если пользователь с таким email не существует.
     */
    UserResponseDto findByEmail(String email);

    /**
     * Получение списка всех зарегистрированных пользователей.
     *
     * @return Список всех пользователей объектов {@link UserResponseDto}
     */
    List<UserResponseDto> findAll();

    /**
     * Изменяет адрес электронной пользователя (email).
     *
     * @param id    уникальный идентификатор пользователя.
     * @param email новый адрес электронной почты.
     * @return Возвращает объект {@link UserResponseDto} с данными измененного пользователя.
     * @throws {@link UserNotFoundException} если пользователь с заданным id не существует.
     */
    UserResponseDto updateEmail(Long id, String email);

    /**
     * Изменяет имя пользователя (username).
     *
     * @param id       уникальный идентификатор пользователя.
     * @param username новое имя пользователя
     * @return Возвращает объект {@link UserResponseDto} с данными измененного пользователя.
     * @throws @throws {@link UserNotFoundException} если пользователь с заданным id не существует.
     */
    UserResponseDto updateUsername(Long id, String username);

    /**
     * Устанавливает новый пароль для пользователя.
     *
     * @param id       уникальный идентификатор пользователя.
     * @param password новый пароль
     * @return Возвращает объект {@link UserResponseDto} с данными измененного пользователя.
     * @throws @throws {@link UserNotFoundException} если пользователь с заданным id не существует.
     */
    UserResponseDto updatePassword(Long id, char[] password);

    /**
     * Удаляет пользователя и все связанные с ним данные из системы.
     *
     * @param id уникальный идентификатор пользователя.
     */
    void deleteById(Long id);
}
