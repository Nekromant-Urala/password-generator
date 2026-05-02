package ru.matthew.NauJava.domain.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryCreateDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntrySpecDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/passwords")
public class PasswordEntryRestController {

    private final PasswordEntryService passwordEntryService;

    @Autowired
    public PasswordEntryRestController(PasswordEntryService passwordEntryService) {
        this.passwordEntryService = passwordEntryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasswordEntryResponseDto> getPasswordEntryById(@PathVariable Long id) {
        var entry = passwordEntryService.findById(id);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/services/{name}")
    public ResponseEntity<List<PasswordEntryResponseDto>> getPasswordEntryByServiceName(@PathVariable(value = "name") String serviceName) {
        var entries = passwordEntryService.findByServiceName(serviceName);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PasswordEntryResponseDto>> getPasswordEntryByUserId(@PathVariable Long userId) {
        var entries = passwordEntryService.findByUserId(userId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PasswordEntryResponseDto>> getAllPasswordEntry() {
        var entries = passwordEntryService.findAll();
        return ResponseEntity.ok(entries);
    }

    @PostMapping("/create")
    public ResponseEntity<PasswordEntryResponseDto> createEntry(@RequestBody PasswordEntryCreateDto dto) {
        var entry = passwordEntryService.createPasswordEntry(dto);
        return new ResponseEntity<>(entry, HttpStatus.CREATED);
    }

    @PutMapping("/update/login/{userId}")
    public ResponseEntity<PasswordEntryResponseDto> updateLogin(@PathVariable Long userId, @RequestBody String login) {
        var entry = passwordEntryService.updateLogin(userId, login);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("update/service/{userId}")
    public ResponseEntity<PasswordEntryResponseDto> updateServiceName(@PathVariable Long userId, @RequestBody String serviceName) {
        var entry = passwordEntryService.updateServiceName(userId, serviceName);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/update/description/{userId}")
    public ResponseEntity<PasswordEntryResponseDto> updateDescription(@PathVariable Long userId, @RequestBody String description) {
        var entry = passwordEntryService.updateDescription(userId, description);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/update/password/{userId}")
    public ResponseEntity<PasswordEntryResponseDto> updatePassword(@PathVariable Long userId, @RequestBody PasswordEntrySpecDto dto) {
        var entry = passwordEntryService.updatePassword(userId, dto);
        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/delete/service/{userId}")
    public ResponseEntity<String> deletePasswordEntryByServiceName(@PathVariable Long userId, @RequestBody String serviceName) {
        passwordEntryService.deleteByServiceName(userId, serviceName);
        return ResponseEntity.ok("Записи связанные с сервисом: '%s' для пользователя с id: '%d' были удалены".formatted(serviceName, userId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePasswordEntryById(@PathVariable Long id) {
        passwordEntryService.deleteById(id);
        return ResponseEntity.ok("Запись с id: '%d' была удалена".formatted(id));
    }
}
