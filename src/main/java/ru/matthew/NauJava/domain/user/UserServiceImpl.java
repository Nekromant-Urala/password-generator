package ru.matthew.NauJava.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.matthew.NauJava.domain.audit.EventType;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.profile.ProfileService;
import ru.matthew.NauJava.domain.user.dto.*;
import ru.matthew.NauJava.domain.user.exception.UserPasswordMissMatchException;
import ru.matthew.NauJava.domain.user.mapper.UserMapper;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.matthew.NauJava.domain.audit.EventType.*;
import static ru.matthew.NauJava.domain.user.Role.USER;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    private final ApplicationEventPublisher eventPublisher;

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(
            UserMapper userMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ProfileService profileService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserResponseDto createUser(UserCreateDto userDto) {
        var user = userMapper.toUser(userDto);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже зарегистрирован");
        }

        user.setRole(USER);
        user.setPassword(passwordEncoder.encode(String.valueOf(userDto.password())));
        var savedUser = userRepository.save(user);
        profileService.createDefaultProfile(savedUser.getId());

        eventPublisher.publishEvent(new AuditEventDto(savedUser.getId(), SIGN_UP_USER, "регистрация пользователя"));
        LOGGER.info("Регистрация нового пользователя с id {}", savedUser.getId());

        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toResponseDto)
                .orElseThrow(
                        () -> new UserNotFoundException("Пользователь по заданному username: '%s' не был найден.".formatted(username))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponseDto)
                .orElseThrow(
                        () -> new UserNotFoundException("Пользователь по заданному email: '%s' не был найден.".formatted(email))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto patchUser(Long id, UserPatchDto dto) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id: '%d' не был найден при частичном обновлении данных".formatted(id))
        );
        if (userRepository.findByUsername(dto.username()).filter(u -> !u.getId().equals(id)).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        if (userRepository.findByEmail(dto.email()).filter(u -> !u.getId().equals(id)).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже зарегистрирован");
        }
        userMapper.updateEntityFromPatchDto(user, dto);
        LOGGER.info("Успешное обновление данных пользователя с id: {}", user.getId());

        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updatePassword(Long id, UserUpdatePasswordDto dto) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id: '%d' не был найден при обновлении password".formatted(id))
        );
        var newPassword = dto.newPassword();
        var oldPassword = dto.oldPassword();

        try {
            if (passwordEncoder.matches(String.valueOf(oldPassword), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(String.valueOf(dto.newPassword())));
                LOGGER.info("успешное обновление пароля пользователя с id:{}", user.getId());
                return userMapper.toResponseDto(user);
            } else {
                throw new UserPasswordMissMatchException("Несоответствие старого пароля");
            }
        } finally {
            Arrays.fill(newPassword, '\0');
            Arrays.fill(oldPassword, '\0');
        }
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Пустой логин");
        }

        var usernameOrEmail = username.trim();
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .map(u -> new CustomUserDetails(
                        u.getId(),
                        u.getUsername(),
                        u.getPassword(),
                        u.getEmail(),
                        u.getRole(),
                        Collections.singleton(u.getRole())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с username: '%s' не был найден".formatted(username)));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        LOGGER.info("Удаление учетной записи пользователя с id: {}", id);
    }
}
