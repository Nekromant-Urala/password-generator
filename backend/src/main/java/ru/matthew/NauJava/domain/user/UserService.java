package ru.matthew.NauJava.domain.user;

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
     * @param userDto данные для создания пользователя.
     * @return Возвращает объект {@link UserResponseDto} с данными созданного пользователя.
     * @throws {@link UserAlreadyExistsException} если пользователь с таким email или username уже существует.
     */
    UserResponseDto createUser(UserCreateDto userDto);

    /**
     * Нахождение пользователя по уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return {@link Optional}, содержащий DTO пользователя, или пустой, если пользователь не найден.
     */
    Optional<UserResponseDto> findById(Long id);

    /**
     * Нахождение пользователя по его имени (username).
     *
     * @param username имя пользователя.
     * @return {@link Optional}, содержащий DTO пользователя, или пустой, если пользователь не найден.
     */
    Optional<UserResponseDto> findByUsername(String username);

    /**
     * Нахождения пользователя по почте (email).
     *
     * @param email адрес электронной почты пользователя.
     * @return {@link Optional}, содержащий DTO пользователя, или пустой, если пользователь не найден.
     */
    Optional<UserResponseDto> findByEmail(String email);

    /**
     * Получение списка всех зарегистрированных пользователей.
     *
     * @return список {@link UserResponseDto}
     */
    List<UserResponseDto> findAll();

    /**
     * Изменяет адрес электронной пользователя (email).
     *
     * @param id    уникальный идентификатор пользователя.
     * @param email новый адрес электронной почты.
     * @throws {@link UserNotFoundException} если пользователь с таким email или username уже существует.
     */
    void updateEmail(Long id, String email);

    /**
     * Изменяет имя пользователя (username).
     *
     * @param id       уникальный идентификатор пользователя.
     * @param username новое имя пользователя
     * @throws {@link UserNotFoundException} если пользователь с таким email или username уже существует.
     */
    void updateUsername(Long id, String username);

    /**
     * Устанавливает новый пароль для пользователя.
     *
     * @param id       уникальный идентификатор пользователя.
     * @param password новый пароль
     * @throws {@link UserNotFoundException} если пользователь с таким email или username уже существует.
     */
    void updatePassword(Long id, char[] password);

    /**
     * Удаляет пользователя и все связанные с ним данные из системы.
     *
     * @param id уникальный идентификатор пользователя.
     */
    void deleteById(Long id);
}
