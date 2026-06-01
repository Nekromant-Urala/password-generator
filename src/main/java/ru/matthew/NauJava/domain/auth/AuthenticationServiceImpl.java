package ru.matthew.NauJava.domain.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.matthew.NauJava.domain.user.UserService;
import ru.matthew.NauJava.domain.user.dto.UserCreateDto;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;

    @Autowired
    public AuthenticationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void singUp(UserCreateDto user) {
        userService.createUser(user);
    }
}
