package ru.matthew.NauJava.service.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.profile.ProfileService;
import ru.matthew.NauJava.domain.user.*;
import ru.matthew.NauJava.domain.user.dto.UserCreateDto;
import ru.matthew.NauJava.domain.user.dto.UserPatchDto;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;
import ru.matthew.NauJava.domain.user.dto.UserUpdatePasswordDto;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;
import ru.matthew.NauJava.domain.user.exception.UserPasswordMissMatchException;
import ru.matthew.NauJava.domain.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ProfileService profileService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Long userId;
    private User savedUser;
    private UserCreateDto userCreateDto;
    private UserResponseDto userResponseDto;

    private User createUser(String username, String email) {
        var newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword("password");
        return newUser;
    }

    @BeforeEach
    public void setUp() {
        userId = 1L;

        userCreateDto = new UserCreateDto("username", "test@test.ru", new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'});
        userResponseDto = new UserResponseDto(userId, "username", "test@test.ru", Role.USER);
        user = createUser("username", "test@test.ru");
        savedUser = createUser("username", "test@test.ru");
        savedUser.setId(userId);
        savedUser.setRole(Role.USER);
        savedUser.setPassword("hash_password");
    }

    @Test
    public void createUser_WhenSuccessCreate_ReturnUserResponseDto() {
        when(userMapper.toUser(userCreateDto)).thenReturn(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hash_password");

        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(userResponseDto);

        var userResponseDtoSaved = userService.createUser(userCreateDto);

        Assertions.assertNotNull(userResponseDtoSaved);
        Assertions.assertEquals(userId, userResponseDtoSaved.id());
        Assertions.assertEquals(userResponseDto.username(), userResponseDtoSaved.username());

        verify(profileService).createDefaultProfile(savedUser.getId());
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void createUser_WhenUsernameAlreadyExists_ReturnUserAlreadyExistsException() {
        when(userMapper.toUser(userCreateDto)).thenReturn(user);
        when(userRepository.findByUsername(anyString())).thenThrow(new UserAlreadyExistsException("Пользователь с таким именем уже существует"));

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    public void createUser_WhenEmailAlreadyExists_ReturnUserAlreadyExistsException() {
        when(userMapper.toUser(userCreateDto)).thenReturn(user);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenThrow(new UserAlreadyExistsException("Пользователь с такой почтой уже зарегистрирован"));

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    public void findById_WhenUserExists_ReturnUserResponseDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(savedUser));
        when(userMapper.toResponseDto(savedUser)).thenReturn(userResponseDto);

        var foundUser = userService.findById(userId);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(userId, foundUser.id());
        Assertions.assertEquals(userResponseDto.username(), foundUser.username());
    }

    @Test
    public void findById_WhenUserDoesNotExists_ReturnUserNotFoundException() {
        Long notExistsId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(notExistsId));
        verify(userMapper, Mockito.never()).toResponseDto(any(User.class));
    }

    @Test
    public void findByUsername_WhenUserExists_ReturnUserResponseDto() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(savedUser));
        when(userMapper.toResponseDto(savedUser)).thenReturn(userResponseDto);

        var foundUser = userService.findByUsername(user.getUsername());

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(userId, foundUser.id());
        Assertions.assertEquals(userResponseDto.username(), foundUser.username());
    }

    @Test
    public void findByUsername_WhenUserDoesNotExists_ReturnUserNotFoundException() {
        String notExistsUsername = "nonExistsUsername";
        when(userRepository.findByUsername(notExistsUsername)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByUsername(notExistsUsername));
        verify(userMapper, Mockito.never()).toResponseDto(any(User.class));
    }

    @Test
    public void findByEmail_WhenUserExists_ReturnUserResponseDto() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(savedUser));
        when(userMapper.toResponseDto(savedUser)).thenReturn(userResponseDto);

        var foundUser = userService.findByEmail(user.getEmail());

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(userId, foundUser.id());
        Assertions.assertEquals(userResponseDto.username(), foundUser.username());
        Assertions.assertEquals(userResponseDto.email(), foundUser.email());
    }

    @Test
    public void findByEmail_WhenUserDoesNotExists_ReturnUserNotFoundException() {
        String notExistsEmail = "test1@test.ru";
        when(userRepository.findByEmail(notExistsEmail)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByEmail(notExistsEmail));
        verify(userMapper, Mockito.never()).toResponseDto(any(User.class));
    }

    @Test
    public void findAll_WhenUsersExists_ReturnUsersList() {
        var user1 = createUser("user1", "email1@test@ru");
        var user2 = createUser("user2", "email2@test@ru");
        var user3 = createUser("user3", "email3@test@ru");

        var userDto1 = new UserResponseDto(1L, "user1", "email1@test@ru", Role.USER);
        var userDto2 = new UserResponseDto(2L, "user2", "email2@test@ru", Role.USER);
        var userDto3 = new UserResponseDto(3L, "user3", "email3@test@ru", Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));
        when(userMapper.toResponseDto(user1)).thenReturn(userDto1);
        when(userMapper.toResponseDto(user2)).thenReturn(userDto2);
        when(userMapper.toResponseDto(user3)).thenReturn(userDto3);

        var foundUser = userService.findAll();

        assertThat(foundUser).isNotNull();
        assertThat(foundUser).hasSize(3);
        assertThat(foundUser).extracting(UserResponseDto::username)
                .containsExactly(user1.getUsername(), user2.getUsername(), user3.getUsername());
    }

    @Test
    public void findAll_WhenUsersDoesNotExists_ReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        var foundUsers = userService.findAll();

        Assertions.assertTrue(foundUsers.isEmpty());
        verify(userMapper, Mockito.never()).toResponseDto(any(User.class));
    }

    @Test
    public void patchUser_WhenUserExists_ReturnUserResponseDto() {
        var update = new UserPatchDto("newUsername", "email@test.ru");

        when(userRepository.findByUsername(update.username())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(update.email())).thenReturn(Optional.empty());

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(savedUser));
        when(userMapper.toResponseDto(savedUser)).thenReturn(userResponseDto);

        var updatedUser = userService.patchUser(userId, update);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(userId, updatedUser.id());

        verify(userMapper).updateEntityFromPatchDto(savedUser, update);
    }

    @Test
    public void patchUser_WhenUserDoesNotExists_ReturnUserNotFoundException() {
        Long notExistsId = 1L;
        var update = new UserPatchDto("newUsername", "email@test.ru");
        when(userRepository.findById(notExistsId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.patchUser(notExistsId, update));

        verify(userMapper, Mockito.never()).updateEntityFromPatchDto(savedUser, update);
        verify(userMapper, Mockito.never()).toResponseDto(savedUser);
    }

    @Test
    public void patchUser_WhenUsernameAlreadyTaken_ReturnUserAlreadyExistsException() {
        var update = new UserPatchDto("newUsername", "email@test.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(savedUser));
        when(userRepository.findByUsername(update.username())).thenReturn(Optional.ofNullable(savedUser));

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.patchUser(userId, update));

        verify(userRepository, Mockito.never()).findByEmail(savedUser.getEmail());
        verify(userMapper, Mockito.never()).updateEntityFromPatchDto(savedUser, update);
        verify(userMapper, Mockito.never()).toResponseDto(savedUser);
    }

    @Test
    public void patchUser_WhenEmailAlreadyTaken_ReturnUserAlreadyExistsException() {
        var update = new UserPatchDto("newUsername", "email@test.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(savedUser));
        when(userRepository.findByEmail(update.email())).thenReturn(Optional.ofNullable(savedUser));
        when(userRepository.findByUsername(update.username())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.patchUser(userId, update));

        verify(userMapper, Mockito.never()).updateEntityFromPatchDto(savedUser, update);
        verify(userMapper, Mockito.never()).toResponseDto(savedUser);
    }

    @Test
    public void updatePassword_WhenUserExists_ReturnUserResponseDto() {
        var update = new UserUpdatePasswordDto(savedUser.getPassword().toCharArray(), "newPassword".toCharArray());
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(savedUser));
        when(passwordEncoder.matches(String.valueOf(update.oldPassword()), savedUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(String.valueOf(update.newPassword()))).thenReturn("new_hash_password");
        when(userMapper.toResponseDto(savedUser)).thenReturn(userResponseDto);

        var updatedUser = userService.updatePassword(userId, update);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(userId, updatedUser.id());
        Assertions.assertEquals(savedUser.getUsername(), updatedUser.username());
        Assertions.assertEquals(savedUser.getEmail(), updatedUser.email());
    }

    @Test
    public void updatePassword_WhenUserDoesNotExists_ReturnUserNotFoundException() {
        var update = new UserUpdatePasswordDto(savedUser.getPassword().toCharArray(), "newPassword".toCharArray());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updatePassword(userId, update));

        verify(passwordEncoder, Mockito.never()).matches(String.valueOf(update.oldPassword()), savedUser.getPassword());
        verify(passwordEncoder, Mockito.never()).encode(String.valueOf(update.newPassword()));
        verify(userMapper, Mockito.never()).toResponseDto(savedUser);
    }

    @Test
    public void updatePassword_WhenPasswordMissMatch_ReturnUserPasswordMissMatchException() {
        var update = new UserUpdatePasswordDto("missMatchPassword".toCharArray(), "newPassword".toCharArray());
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(savedUser));
        when(passwordEncoder.matches(String.valueOf(update.oldPassword()), savedUser.getPassword())).thenReturn(false);

        Assertions.assertThrows(UserPasswordMissMatchException.class, () -> userService.updatePassword(userId, update));

        verify(passwordEncoder, Mockito.never()).encode(String.valueOf(update.newPassword()));
        verify(userMapper, Mockito.never()).toResponseDto(savedUser);
    }

    @Test
    public void loadUserByUsername_WhenUserExistsLoadByUsername_ReturnUserDetails() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(savedUser));

        var foundUser = userService.loadUserByUsername(savedUser.getUsername());

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(savedUser.getUsername(), foundUser.getUsername());

        verify(userRepository, Mockito.never()).findByEmail(savedUser.getEmail());
    }

    @Test
    public void loadUserByUsername_WhenUserExistsLoadByEmail_ReturnUserDetails() {
        when(userRepository.findByUsername(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(savedUser));

        var foundUser = userService.loadUserByUsername(savedUser.getEmail());

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(savedUser.getUsername(), foundUser.getUsername());
        Assertions.assertEquals(savedUser.getPassword(), foundUser.getPassword());
    }

    @Test
    public void loadUserByUsername_WhenNullLogin_ReturnUsernameNotFoundException() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(null));

        verify(userRepository, Mockito.never()).findByEmail(savedUser.getEmail());
        verify(userRepository, Mockito.never()).findByEmail(savedUser.getEmail());
    }

    @Test
    public void loadUserByUsername_WhenEmptyLogin_ReturnUsernameNotFoundException() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(""));

        verify(userRepository, Mockito.never()).findByEmail(savedUser.getEmail());
        verify(userRepository, Mockito.never()).findByEmail(savedUser.getEmail());
    }

    @Test
    public void loadUserByUsername_WhenUserDoesNotExists_ReturnUserDetails() {
        String notExistsUsername = "notExists";

        when(userRepository.findByUsername(notExistsUsername)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(notExistsUsername)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(notExistsUsername));
    }

    @Test
    public void deleteById_ShouldDeleteTargetUser() {
        userService.deleteById(userId);

        verify(userRepository).deleteById(userId);
    }
}
