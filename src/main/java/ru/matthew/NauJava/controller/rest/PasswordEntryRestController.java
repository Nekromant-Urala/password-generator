package ru.matthew.NauJava.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.service.PasswordEntryService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/passwords")
public class PasswordEntryRestController {

    private final PasswordEntryService passwordEntryService;

    @Autowired
    public PasswordEntryRestController(PasswordEntryService passwordEntryService) {
        this.passwordEntryService = passwordEntryService;
    }

    @GetMapping("/findByCreatedAtBetween")
    public List<PasswordEntry> findByCreatedAtBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return passwordEntryService.findByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
    }

    @GetMapping("/findByServiceName")
    public List<PasswordEntry> findByServiceName(@RequestParam String serviceName){
        return passwordEntryService.findByServiceName(serviceName);
    }

    @DeleteMapping("/{serviceName}")
    public ResponseEntity<Void> deleteByServiceName(@PathVariable String serviceName){
        passwordEntryService.deleteByServiceName(serviceName);
        return ResponseEntity.noContent().build();
    }
}
