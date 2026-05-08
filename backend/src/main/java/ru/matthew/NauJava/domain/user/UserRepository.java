package ru.matthew.NauJava.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Интерфейс репозитория для доступа к данным пользователей в базе данных.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Нахождение пользователя по его имени (username).
     *
     * @param username имя пользователя.
     * @return {@link Optional} с найденным пользователем, или {@link Optional#empty()}, если пользователь не найден
     */
    Optional<User> findByUsername (String username);

    /**
     * Нахождение пользователя по его имени (username).
     *
     * @param email адрес электронной почты пользователя.
     * @return {@link Optional} с найденным пользователем, или {@link Optional#empty()}, если пользователь не найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет существование пользователя в БД по его имени (username)
     *
     * @param username имя пользователя.
     * @return {@code true}, если пользователь существует в противном случае {@code false}
     */
    boolean existsByUsername(String username);

    /**
     * Проверяет существование пользователя в БД по его
     * электронной почте (email)
     *
     * @param email электронная почта пользователя
     * @return {@code true}, если пользователь существует в противном случае {@code false}
     */
    boolean existsByEmail(String email);
}
