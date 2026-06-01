package ru.matthew.NauJava.service.IT;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.audit.EventType;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.profile.ProfileServiceImpl;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.UserService;
import ru.matthew.NauJava.domain.user.UserServiceImpl;
import ru.matthew.NauJava.domain.user.dto.UserCreateDto;
import ru.matthew.NauJava.domain.user.dto.UserPatchDto;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;
import ru.matthew.NauJava.domain.user.dto.UserUpdatePasswordDto;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;
import ru.matthew.NauJava.domain.user.exception.UserPasswordMissMatchException;

import static ru.matthew.NauJava.domain.user.Role.USER;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
public class UserServiceIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private ProfileServiceImpl profileService;
    @Autowired
    private ApplicationEvents applicationEvents;
    @Autowired
    private UserServiceImpl userService;

    @Nested
    class CreateUserTests {
        @Test
        public void createUser_WhenSuccessCreate_ReturnUserResponseDto() {
            String username = "username";
            String email = "test@mail.ru";
            String password = "password";

            var createDto = new UserCreateDto(username, email, password.toCharArray());

            var response = userService.createUser(createDto);

            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.username()).isEqualTo(username);
            Assertions.assertThat(response.email()).isEqualTo(email);
            Assertions.assertThat(response.role()).isEqualTo(USER);

            var savedUser = userRepository.findById(response.id()).orElseThrow();
            Assertions.assertThat(savedUser.getUsername()).isEqualTo(username);

            Assertions.assertThat(passwordEncoder.matches(password, savedUser.getPassword()));

            Mockito.verify(profileService).createDefaultProfile(savedUser.getId());

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.SIGN_UP_USER);
        }

        @Test
        public void createUser_WhenUsernameExists_ThrowUserAlreadyExistsException() {
            var existsUser = createUser();
            userRepository.save(existsUser);

            var existsUsername = "username";
            var uniqueEmail = "unique@mail.ru";
            var passwordOfUserExists = "hash_password";
            var createDto = new UserCreateDto(existsUsername, uniqueEmail, passwordOfUserExists.toCharArray());

            Assertions.assertThatThrownBy(() -> userService.createUser(createDto))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("Пользователь с таким именем уже существует");
        }

        @Test
        public void createUser_WhenEmailExists_ThrowUserAlreadyExistsException() {
            var existsUser = createUser();
            userRepository.save(existsUser);

            var uniqueUsername = "unique";
            var existsEmail = "test@mail.ru";
            var passwordOfUserExists = "hash_password";
            var createDto = new UserCreateDto(uniqueUsername, existsEmail, passwordOfUserExists.toCharArray());

            Assertions.assertThatThrownBy(() -> userService.createUser(createDto))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("Пользователь с такой почтой уже зарегистрирован");
        }
    }

    @Nested
    class FindUserTests {

        @Test
        public void findById_WhenUserExists_ReturnUserResponseDto() {
            var existsUser = createUser();
            existsUser = userRepository.save(existsUser);

            var foundUser = userService.findById(existsUser.getId());

            Assertions.assertThat(foundUser).isNotNull();
            Assertions.assertThat(foundUser.id()).isEqualTo(existsUser.getId());
            Assertions.assertThat(foundUser.username()).isEqualTo(existsUser.getUsername());
            Assertions.assertThat(foundUser.email()).isEqualTo(existsUser.getEmail());
        }

        @Test
        public void findById_WhenUserDoesNotExists_ThrowUserNotFoundException() {
            var nonExistentId = 999L;

            Assertions.assertThatThrownBy(() -> userService.findById(nonExistentId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь по заданному id: '%d' не был найден.".formatted(nonExistentId));
        }

        @Test
        public void findByUsername_WhenUserExists_ReturnUserResponseDto() {
            var existsUser = createUser();
            existsUser = userRepository.save(existsUser);

            var foundUser = userService.findByUsername(existsUser.getUsername());

            Assertions.assertThat(foundUser).isNotNull();
            Assertions.assertThat(foundUser.id()).isEqualTo(existsUser.getId());
            Assertions.assertThat(foundUser.username()).isEqualTo(existsUser.getUsername());
            Assertions.assertThat(foundUser.email()).isEqualTo(existsUser.getEmail());
        }

        @Test
        public void findByUsername_WhenUserDoesNotExists_ThrowUserNotFoundException() {
            var nonExistentUsername = "nonExistent";

            Assertions.assertThatThrownBy(() -> userService.findByUsername(nonExistentUsername))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь по заданному username: '%s' не был найден.".formatted(nonExistentUsername));
        }

        @Test
        public void findByEmail_WhenUserExists_ReturnUserResponseDto() {
            var existsUser = createUser();
            existsUser = userRepository.save(existsUser);

            var foundUser = userService.findByEmail(existsUser.getEmail());

            Assertions.assertThat(foundUser).isNotNull();
            Assertions.assertThat(foundUser.id()).isEqualTo(existsUser.getId());
            Assertions.assertThat(foundUser.username()).isEqualTo(existsUser.getUsername());
            Assertions.assertThat(foundUser.email()).isEqualTo(existsUser.getEmail());
        }

        @Test
        public void findByEmail_WhenUserDoesNotExists_ThrowUserNotFoundException() {
            var nonExistentEmail = "non.existent@mail.ru";

            Assertions.assertThatThrownBy(() -> userService.findByEmail(nonExistentEmail))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь по заданному email: '%s' не был найден.".formatted(nonExistentEmail));
        }

        @Test
        public void findAll_WhenUserExists_ReturnUserResponseDto() {
            var existsUser1 = createUser();
            existsUser1.setUsername("user1");
            existsUser1.setEmail("test1@mail.ru");
            var existsUser2 = createUser();
            existsUser2.setUsername("user2");
            existsUser2.setEmail("test2@mail.ru");
            var existsUser3 = createUser();
            existsUser3.setUsername("user3");
            existsUser3.setEmail("test3@mail.ru");

            userRepository.save(existsUser1);
            userRepository.save(existsUser2);
            userRepository.save(existsUser3);

            var foundUsers = userService.findAll();

            Assertions.assertThat(foundUsers).isNotNull();
            Assertions.assertThat(foundUsers).hasSize(3);
            Assertions.assertThat(foundUsers).extracting(UserResponseDto::username)
                    .containsExactly(
                            existsUser1.getUsername(),
                            existsUser2.getUsername(),
                            existsUser3.getUsername()
                    );
        }

        @Test
        public void findAll_WhenUserDoesNotExists_ReturnEmptyList() {
            var foundUsers = userService.findAll();

            Assertions.assertThat(foundUsers).isNotNull();
            Assertions.assertThat(foundUsers.isEmpty()).isTrue();
        }
    }

    @Nested
    class PatchUpdateUserTest {
        private User existUser;

        @BeforeEach
        public void prepareData() {
            existUser = createUser();
            existUser = userRepository.save(existUser);
        }

        @Test
        public void patchUser_WhenValidUsernameAndEmailEmpty_ReturnUserResponseDto() {
            var correctUsername = "correctUsername";
            var oldEmail = existUser.getEmail();
            var oldId = existUser.getId();

            var update = new UserPatchDto(correctUsername, "");

            var updatedUser = userService.patchUser(existUser.getId(), update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(oldId);
            Assertions.assertThat(updatedUser.email()).isEqualTo(oldEmail);
            Assertions.assertThat(updatedUser.username()).isEqualTo(correctUsername);
        }

        @Test
        public void patchUser_WhenValidUsernameAndEmailNull_ReturnUserResponseDto() {
            var correctUsername = "correctUsername";
            var oldEmail = existUser.getEmail();
            var oldId = existUser.getId();

            var update = new UserPatchDto(correctUsername, null);

            var updatedUser = userService.patchUser(existUser.getId(), update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(oldId);
            Assertions.assertThat(updatedUser.email()).isEqualTo(oldEmail);
            Assertions.assertThat(updatedUser.username()).isEqualTo(correctUsername);
        }

        @Test
        public void patchUser_WhenValidEmailAndUsernameNull_ReturnUserResponseDto() {
            var correctEmail = "new@mail.ru";
            var oldUsername = existUser.getUsername();
            var oldId = existUser.getId();

            var update = new UserPatchDto(null, correctEmail);

            var updatedUser = userService.patchUser(existUser.getId(), update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(oldId);
            Assertions.assertThat(updatedUser.email()).isEqualTo(correctEmail);
            Assertions.assertThat(updatedUser.username()).isEqualTo(oldUsername);
        }

        @Test
        public void patchUser_WhenValidEmailAndUsernameEmpty_ReturnUserResponseDto() {
            var correctEmail = "new@mail.ru";
            var oldUsername = existUser.getUsername();
            var oldId = existUser.getId();

            var update = new UserPatchDto("", correctEmail);

            var updatedUser = userService.patchUser(existUser.getId(), update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(oldId);
            Assertions.assertThat(updatedUser.email()).isEqualTo(correctEmail);
            Assertions.assertThat(updatedUser.username()).isEqualTo(oldUsername);
        }

        @Test
        public void patchUser_WhenValidEmailAndUsername_ReturnUserResponseDto() {
            var correctEmail = "new@mail.ru";
            var correctUsername = "correctUsername";
            var oldId = existUser.getId();

            var update = new UserPatchDto(correctUsername, correctEmail);

            var updatedUser = userService.patchUser(existUser.getId(), update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(oldId);
            Assertions.assertThat(updatedUser.email()).isEqualTo(correctEmail);
            Assertions.assertThat(updatedUser.username()).isEqualTo(correctUsername);
        }

        @Test
        public void patchUser_WhenEmailAndUsernameNull_ReturnUserResponseDto() {
            var oldEmail = existUser.getEmail();
            var oldUsername = existUser.getUsername();
            var oldId = existUser.getId();

            var update = new UserPatchDto(null, null);

            var updatedUser = userService.patchUser(existUser.getId(), update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(oldId);
            Assertions.assertThat(updatedUser.email()).isEqualTo(oldEmail);
            Assertions.assertThat(updatedUser.username()).isEqualTo(oldUsername);
        }

        @Test
        public void patchUser_WhenEmailAndUsernameEmpty_ReturnUserResponseDto() {
            var oldEmail = existUser.getEmail();
            var oldUsername = existUser.getUsername();
            var oldId = existUser.getId();

            var update = new UserPatchDto("", "");

            var updatedUser = userService.patchUser(existUser.getId(), update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(oldId);
            Assertions.assertThat(updatedUser.email()).isEqualTo(oldEmail);
            Assertions.assertThat(updatedUser.username()).isEqualTo(oldUsername);
        }

        @Test
        public void patchUser_WhenUserDoesNotExists_ThrowUserNotFoundException() {
            var nonExistentId = 999L;
            var update = new UserPatchDto("newUsername", "newEmail@mail.ru");

            Assertions.assertThatThrownBy(() -> userService.patchUser(nonExistentId, update))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь с id: '%d' не был найден при частичном обновлении данных".formatted(nonExistentId));
        }

        @Test
        public void patchUser_WhenUserAlreadyExistsWithUsername_ThrowUserAlreadyExistsException() {
            var oldEmail = existUser.getEmail();
            var oldUsername = existUser.getUsername();
            var oldId = existUser.getId();

            var update = new UserPatchDto("username", "");

            Assertions.assertThatThrownBy(() -> userService.patchUser(existUser.getId(), update))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("Пользователь с таким именем уже существует");

            Assertions.assertThat(existUser.getId()).isEqualTo(oldId);
            Assertions.assertThat(existUser.getEmail()).isEqualTo(oldEmail);
            Assertions.assertThat(existUser.getUsername()).isEqualTo(oldUsername);
        }

        @Test
        public void patchUser_WhenUserAlreadyExistsWithEmail_ThrowUserAlreadyExistsException() {
            var oldEmail = existUser.getEmail();
            var oldUsername = existUser.getUsername();
            var oldId = existUser.getId();

            var update = new UserPatchDto("", "test@mail.ru");

            Assertions.assertThatThrownBy(() -> userService.patchUser(existUser.getId(), update))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("Пользователь с такой почтой уже зарегистрирован");

            Assertions.assertThat(existUser.getId()).isEqualTo(oldId);
            Assertions.assertThat(existUser.getEmail()).isEqualTo(oldEmail);
            Assertions.assertThat(existUser.getUsername()).isEqualTo(oldUsername);
        }
    }

    @Nested
    class UpdatePasswordTests {

        private User existUser;

        @BeforeEach
        public void preparedData() {
            existUser = createUser();
            existUser = userRepository.save(existUser);
        }

        @Test
        public void updatePassword_WhenUserDoesNotExists_ThrowUserNotFoundException() {
            var nonExistentUserId = 999L;
            var oldPassword = "password";
            var newPassword = "password_new";

            var update = new UserUpdatePasswordDto(oldPassword.toCharArray(), newPassword.toCharArray());

            Assertions.assertThatThrownBy(() -> userService.updatePassword(nonExistentUserId, update))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь с id: '%d' не был найден при обновлении password".formatted(nonExistentUserId));
        }

        @Test
        public void updatePassword_WhenPasswordMissMatch_ThrowUserPasswordMissMatchException() {
            var userId = existUser.getId();
            var oldPassword = "password";
            var newPassword = "password_new";

            var update = new UserUpdatePasswordDto("fakePassword".toCharArray(), newPassword.toCharArray());

            Assertions.assertThatThrownBy(() -> userService.updatePassword(userId, update))
                    .isInstanceOf(UserPasswordMissMatchException.class)
                    .hasMessageContaining("Несоответствие старого пароля");

            var foundUser = userRepository.findById(userId).orElseThrow();
            Assertions.assertThat(passwordEncoder.matches(oldPassword, foundUser.getPassword()));
        }

        @Test
        public void updatePassword_WhenUserExistsAndValidPassword_WhenUserResponseDto() {
            var userId = existUser.getId();
            var oldUsername = existUser.getUsername();
            var oldPassword = "password";
            var newPassword = "password_new";

            var update = new UserUpdatePasswordDto(oldPassword.toCharArray(), newPassword.toCharArray());

            var updatedUser = userService.updatePassword(userId, update);

            Assertions.assertThat(updatedUser).isNotNull();
            Assertions.assertThat(updatedUser.id()).isEqualTo(userId);
            Assertions.assertThat(updatedUser.username()).isEqualTo(oldUsername);

            var foundUser = userRepository.findById(userId).orElseThrow();
            Assertions.assertThat(passwordEncoder.matches(newPassword, foundUser.getPassword()));
        }
    }

    @Nested
    class LoadUserDetailsTests {
        private User existUser;

        @Test
        public void loadUserByUsername_WhenEmptyUsername_ThrowUsernameNotFoundException() {
            var nonExistentUsername = "";

            Assertions.assertThatThrownBy(() -> userService.loadUserByUsername(nonExistentUsername))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Пустой логин");
        }

        @Test
        public void loadUserByUsername_WhenUserDoesNotExists_ThrowUsernameNotFoundException() {
            var nonExistentUsername = "nonExistentUsername";

            Assertions.assertThatThrownBy(() -> userService.loadUserByUsername(nonExistentUsername))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Пользователь с username: '%s' не был найден".formatted(nonExistentUsername));
        }

        @Test
        public void loadUserByUsername_WhenUserExists_ReturnCustomUserDetails() {
            existUser = createUser();
            existUser = userRepository.save(existUser);

            var userId = existUser.getId();
            var username = existUser.getUsername();

            var foundUser = userService.loadUserByUsername(username);

            Assertions.assertThat(foundUser).isNotNull();
            Assertions.assertThat(foundUser.id()).isEqualTo(userId);
            Assertions.assertThat(foundUser.username()).isEqualTo(username);
            Assertions.assertThat(foundUser.password()).isEqualTo(existUser.getPassword());
        }
    }

    @Nested
    class DeleteUserTests {
        @Test
        public void deleteById_WhenUserExists_ShouldDeleteTargetUser() {
            var existUser = createUser();
            existUser = userRepository.save(existUser);
            var userId = existUser.getId();

            userService.deleteById(userId);

            Assertions.assertThatThrownBy(() -> userService.findById(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь по заданному id: '%d' не был найден.".formatted(userId));
        }
    }

    private User createUser() {
        var user = new User();
        user.setUsername("username");
        user.setEmail("test@mail.ru");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(USER);
        return user;
    }
}
