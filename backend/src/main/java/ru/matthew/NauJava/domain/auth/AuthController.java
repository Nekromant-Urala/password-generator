package ru.matthew.NauJava.domain.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.matthew.NauJava.domain.user.dto.UserCreateDto;
import ru.matthew.NauJava.domain.user.UserService;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserCreateDto dto) {
        var userDto = userService.createUser(dto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

}
