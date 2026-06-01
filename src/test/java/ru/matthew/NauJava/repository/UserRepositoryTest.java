package ru.matthew.NauJava.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

    private UserRepository userRepository;
    private TestEntityManager entityManager;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, TestEntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    private User createUser(String username, String email, String password) {
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    @Test
    public void save_ReturnSavedUser() {
        var user = createUser("username", "email@test.ru", "hash_password");

        var savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void findById_WhenUserExists_ReturnUser() {
        var user = createUser("username", "email@test.ru", "hash_password");
        entityManager.persistAndFlush(user);

        var foundUser = userRepository.findById(user.getId());

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getUsername()).isEqualTo("username");
    }

    @Test
    public void findAll_WhenUsersExists_ReturnUsers() {
        var user1 = createUser("username1", "email@test1.ru", "hash_password1");
        var user2 = createUser("username2", "email@test2.ru", "hash_password2");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        var users = userRepository.findAll();

        Assertions.assertThat(users).isNotNull();
        Assertions.assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void deleteById_WhenUserExists_ShouldDeleteTagetUser() {
        var user = createUser("username", "email@test.ru", "hash_password");
        entityManager.persistAndFlush(user);

        userRepository.deleteById(user.getId());
        var foundUser = userRepository.findById(user.getId());

        Assertions.assertThat(foundUser).isEmpty();
    }

    @Test
    public void findByUsername_WhenUserExists_ReturnUser() {
        var user = createUser("usernameTest", "email@test.ru", "hash_password");
        entityManager.persistAndFlush(user);

        var foundUser = userRepository.findByUsername("usernameTest");

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getUsername()).isEqualTo("usernameTest");
        Assertions.assertThat(foundUser.get().getCreatedAt()).isNotNull();
    }

    @Test
    public void findByUsername_WhenUserDoesNotExists_ReturnEmptyOptional() {
        var user = userRepository.findByUsername("non-exists name");

        Assertions.assertThat(user).isEmpty();
    }

    @Test
    public void findByEmail_WhenUserExists_ReturnUser() {
        var user = createUser("username", "email@test.ru", "hash_password");
        entityManager.persistAndFlush(user);

        var foundUser = userRepository.findByEmail("email@test.ru");

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getEmail()).isEqualTo("email@test.ru");
    }

    @Test
    public void findByEmail_WhenUserDoesNotExists_ReturnEmptyOptional() {
        var user = userRepository.findByEmail("non.exists.email@test.ru");

        Assertions.assertThat(user).isEmpty();
    }
}
