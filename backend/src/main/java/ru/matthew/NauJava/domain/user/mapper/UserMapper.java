package ru.matthew.NauJava.domain.user.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.dto.UserCreateDto;
import ru.matthew.NauJava.domain.user.dto.UserForPasswordEntryDto;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;

@Component
public class UserMapper {

    public User toUser(UserCreateDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        return user;
    }

    public User toUser(UserForPasswordEntryDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.id());
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        return user;
    }

    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public UserForPasswordEntryDto toUserForPasswordEntryDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserForPasswordEntryDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

}
