package ru.matthew.NauJava.domain.user.mapper;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.dto.*;

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

    public void updateEntityFromPatchDto(User user, UserPatchDto dto) {
        if (user == null || dto == null) {
            return;
        }
        if (dto.username() != null && !dto.username().isEmpty()) {
            user.setUsername(dto.username());
        }
        if (dto.email() != null && !dto.email().isEmpty()) {
            user.setEmail(dto.email());
        }
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
}
