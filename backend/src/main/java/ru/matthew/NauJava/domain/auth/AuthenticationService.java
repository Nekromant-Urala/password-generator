package ru.matthew.NauJava.domain.auth;

import ru.matthew.NauJava.domain.user.dto.UserCreateDto;

public interface AuthenticationService {

    /**
     *
     * @param user
     */
    void register(UserCreateDto user);
}
