package ru.matthew.NauJava.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto userCreateDto) {
        var user = userService.createUser(userCreateDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable(name = "id") Long userId) {
        var user = userService.findById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username
    ) {
        if (email != null) {
            var user = userService.findByEmail(email);
            return new ResponseEntity<>(List.of(user), HttpStatus.OK);
        }
        if (username != null) {
            var user = userService.findByUsername(username);
            return new ResponseEntity<>(List.of(user), HttpStatus.OK);
        }
        var users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PatchMapping("/me/details")
    public ResponseEntity<UserResponseDto> patchUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserPatchDto dto
    ) {
        var user = userService.patchUser(userDetails.id(), dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/me/password")
    public ResponseEntity<UserResponseDto> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdatePasswordDto passwordDto
    ) {
        try {
            var user = userService.updatePassword(userDetails.id(), passwordDto);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } finally {
            Arrays.fill(passwordDto.newPassword(), '\0');
            Arrays.fill(passwordDto.oldPassword(), '\0');
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.deleteById(userDetails.id());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
