package ru.matthew.NauJava.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.matthew.NauJava.domain.user.dto.UserResponseDto;
import ru.matthew.NauJava.domain.user.dto.UserUpdateEmailDto;
import ru.matthew.NauJava.domain.user.dto.UserUpdatePasswordDto;
import ru.matthew.NauJava.domain.user.dto.UserUpdateUsernameDto;

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

    @GetMapping("/search/email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam String email) {
        var user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search/username")
    public ResponseEntity<UserResponseDto> getUserByUsername(@RequestParam String username) {
        var user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        var users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<UserResponseDto> updateUsername(@PathVariable Long id, @RequestBody UserUpdateUsernameDto req) {
        var dto = userService.updateUsername(id, req.username());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserResponseDto> updateEmail(@PathVariable Long id, @RequestBody UserUpdateEmailDto req) {
        var dto = userService.updateEmail(id, req.email());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable Long id, @RequestBody UserUpdatePasswordDto req) {
        try {
            var dto = userService.updatePassword(id, req.password());
            return ResponseEntity.ok(dto);
        } finally {
            Arrays.fill(req.password(), '\0');
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
