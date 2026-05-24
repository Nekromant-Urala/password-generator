package ru.matthew.NauJava.domain.auth;

import ru.matthew.NauJava.domain.user.dto.UserCreateDto;

public interface AuthenticationService {

    /**
     * Регистрация нового пользователя
     *
     * @param user объект типа {@link UserCreateDto} содержащий данные для создания пользователя
     */
    void singUp(UserCreateDto user);
}
