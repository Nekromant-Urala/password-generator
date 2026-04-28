package ru.matthew.NauJava.domain.user;

import java.util.Optional;

public interface UserService {

    /**
     * Ищет пользователя по его имени
     *
     * @param username имя пользователя
     * @return {@link Optional} с найденным пользователем, или {@link Optional#empty()}, если пользователь не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Создает нового пользователя в системе.
     *
     * @param user пользователй, которого необходимо сохранить
     * @return созданный объект {@link User}
     */
    User createUser(User user);
}
