package ru.matthew.NauJava.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        var user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/name/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        var user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        var users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/username/{id}")
    public ResponseEntity<UserResponseDto> updateUsername(@PathVariable Long id, @RequestBody String username) {
        var dto = userService.updateUsername(id, username);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/update/email/{id}")
    public ResponseEntity<UserResponseDto> updateEmail(@PathVariable Long id, @RequestBody String email) {
        var dto = userService.updateEmail(id, email);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/update/password/{id}")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable Long id, @RequestBody char[] password) {
        try {
            var dto = userService.updatePassword(id, password);
            return ResponseEntity.ok(dto);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok("Пользователь с id: '%d'был удален".formatted(id));
    }
}
