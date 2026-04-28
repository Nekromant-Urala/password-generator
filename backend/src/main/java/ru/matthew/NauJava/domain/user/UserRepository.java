package ru.matthew.NauJava.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по его имени
     *
     * @param username имя пользователя
     * @return {@link Optional} с найденным пользователем, или {@link Optional#empty()}, если пользователь не найден
     */
    Optional<User> findByUsername(String username);
}
