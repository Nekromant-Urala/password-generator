package ru.matthew.NauJava.domain.user;

import ru.matthew.NauJava.domain.user.dto.*;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;
import ru.matthew.NauJava.domain.user.exception.UserPasswordMissMatchException;

import java.util.List;

/**
 * Интерфейс сервиса для управления пользователями.
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
     * Частичное обновление данных пользователя.
     * Доступно обновления электронной почты (email) или имени пользователя (username)
     *
     * @param id  уникальный идентификатор пользователя
     * @param dto данные для обновления
     * @return Возвращает объект {@link UserResponseDto} с данными измененного пользователя.
     * @throws {@link UserNotFoundException} если пользователь с заданным id не существует.
     */
    UserResponseDto patchUser(Long id, UserPatchDto dto);

    /**
     * Устанавливает новый пароль для пользователя.
     *
     * @param id  уникальный идентификатор пользователя.
     * @param dto данные для обновления
     * @return Возвращает объект {@link UserResponseDto} с данными измененного пользователя.
     * @throws {@link UserNotFoundException} если пользователь с заданным id не существует. {@link UserPasswordMissMatchException} если старый пароль не совпал с паролем хранящимся в базе данных
     */
    UserResponseDto updatePassword(Long id, UserUpdatePasswordDto dto);

    /**
     * Удаляет пользователя и все связанные с ним данные из системы.
     *
     * @param id уникальный идентификатор пользователя.
     */
    void deleteById(Long id);
}
