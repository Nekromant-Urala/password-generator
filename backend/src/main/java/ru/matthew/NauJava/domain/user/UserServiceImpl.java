package ru.matthew.NauJava.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.matthew.NauJava.domain.user.Role.USER;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto createUser(UserCreateDto userDto) {
        var user = userMapper.toUser(userDto);
        if (userRepository.findById(user.getId()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь уже существует");
        }

        user.setRole(USER);
        user.setPassword(passwordEncoder.encode(String.valueOf(userDto.password())));
        userRepository.save(user);

        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponseDto).toList();
    }

    @Override
    public void updateEmail(Long id, String email) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id: '%d' не был найден при обновлении email".formatted(id))
        );
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void updateUsername(Long id, String username) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id: '%d' не был найден при обновлении username".formatted(id))
        );
        user.setUsername(username);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(Long id, char[] password) {
        var user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id: '%d' не был найден при обновлении password".formatted(id))
        );
        try {
            user.setPassword(passwordEncoder.encode(String.valueOf(password)));
            userRepository.save(user);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(user.getRole())
                ))
                .orElseThrow(() -> new UserNotFoundException("Пользователь с username: '%s' не был найден".formatted(username)));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
