package ru.matthew.NauJava.domain.password;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ru.matthew.NauJava.domain.password.dto.*;
import ru.matthew.NauJava.domain.user.CustomUserDetails;


@RestController
@RequestMapping("/passwords")
public class PasswordEntryRestController {

    private final PasswordEntryService passwordEntryService;

    @Autowired
    public PasswordEntryRestController(PasswordEntryService passwordEntryService) {
        this.passwordEntryService = passwordEntryService;
    }

    @PostMapping
    public ResponseEntity<PasswordEntryResponseDto> createEntry(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordEntryRequestDto dto
    ) {
        var entry = passwordEntryService.createPasswordEntry(userDetails.id(), dto);
        return new ResponseEntity<>(entry, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasswordEntryResponseDto> getEntryById(@PathVariable(name = "id") Long entryId) {
        var entry = passwordEntryService.findById(entryId);
        return new ResponseEntity<>(entry, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PasswordEntryResponseDto>> getEntries(
            @Valid PasswordEntrySearchFilterDto filter,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        if (filter.serviceName() != null && !filter.serviceName().isEmpty()) {
            var entries = passwordEntryService.findByServiceName(userDetails.id(), filter.serviceName(), pageable);
            return new ResponseEntity<>(entries, HttpStatus.OK);
        } else if (filter.createdAt() != null) {
            var entries = passwordEntryService.findByCreatedAt(userDetails.id(), filter.createdAt(), pageable);
            return new ResponseEntity<>(entries, HttpStatus.OK);
        }
        if (filter.startDate() != null && filter.endDate() != null) {
            var entries = passwordEntryService.findByCreatedAtBetween(userDetails.id(), filter.startDate(), filter.endDate(), pageable);
            return new ResponseEntity<>(entries, HttpStatus.OK);
        }
        if (filter.updatedAt() != null) {
            var entries = passwordEntryService.findByUpdatedAt(userDetails.id(), filter.updatedAt(), pageable);
            return new ResponseEntity<>(entries, HttpStatus.OK);
        }

        var entries = passwordEntryService.findAllByPageForUser(userDetails.id(), pageable);
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PasswordEntryResponseDto> updateDetails(
            @PathVariable(name = "id") Long entryId,
            @Valid @RequestBody PasswordEntryUpdateDto updateDto
    ) {
        var entry = passwordEntryService.updatePatchEntry(entryId, updateDto);
        return new ResponseEntity<>(entry, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCountAllEntryByUserId(@AuthenticationPrincipal CustomUserDetails userDetails) {
        var total = passwordEntryService.countAllEntryByUserId(userDetails.id());
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @GetMapping("/{id}/reveal")
    public ResponseEntity<PasswordResponseDto> getRevealPassword(@PathVariable(name = "id") Long entryId) {
        var password = passwordEntryService.revealPassword(entryId);
        return new ResponseEntity<>(password, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEntries(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid PasswordEntryDeleteFilterDto filter
    ) {
        if (filter.serviceName() != null) {
            passwordEntryService.deleteByServiceName(userDetails.id(), filter.serviceName());
        } else if (filter.startDate() != null && filter.endDate() != null) {
            passwordEntryService.deleteByCreatedAtBetween(userDetails.id(), filter.startDate(), filter.endDate());
        } else {
            passwordEntryService.deleteByUserId(userDetails.id());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "id") Long entryId
    ) {
        passwordEntryService.deleteById(userDetails.id(), entryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
