package ru.matthew.NauJava.domain.security.auth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.matthew.NauJava.domain.user.dto.UserSingUpRequestDto;
import ru.matthew.NauJava.domain.user.UserService;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sing-up")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody UserSingUpRequestDto dto) {
        var userDto = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PostMapping("/sing-in")
    //TODO доделать вход пользователя
    public ResponseEntity<UserResponseDto> signIn(@RequestBody UserSingUpRequestDto dto) {
        return null;
    }

}
