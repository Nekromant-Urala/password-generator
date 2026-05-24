package ru.matthew.NauJava.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.matthew.NauJava.domain.user.dto.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto userCreateDto) {
        var user = userService.createUser(userCreateDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable(name = "id") Long userId) {
        var user = userService.findById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @GetMapping(value = "/search", params = "email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam String email) {
        var user = userService.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/search", params = "username")
    public ResponseEntity<UserResponseDto> getUserByUsername(@RequestParam String username) {
        var user = userService.findByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        var users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> patchUser(@PathVariable(name = "id") Long userId, @RequestBody UserPatchDto dto) {
        var user = userService.patchUser(userId, dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<UserResponseDto> updateFullUser(@PathVariable(name = "id") Long userId, @RequestBody UserUpdateFullDto dto) {
        var user = userService.updateFullUser(userId, dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable(name = "id") Long userId, @RequestBody UserUpdatePasswordDto passwordDto) {
        try {
            var user = userService.updatePassword(userId, passwordDto);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } finally {
            Arrays.fill(passwordDto.newPassword(), '\0');
            Arrays.fill(passwordDto.oldPassword(), '\0');
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long userId) {
        userService.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
