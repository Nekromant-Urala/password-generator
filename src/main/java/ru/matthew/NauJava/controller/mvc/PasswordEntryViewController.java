package ru.matthew.NauJava.controller.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.service.PasswordEntryService;

import java.util.List;

@Controller
@RequestMapping("/passwords/view")
public class PasswordEntryViewController {

    private final PasswordEntryService passwordEntryService;

    @Autowired
    public PasswordEntryViewController(PasswordEntryService passwordEntryService) {
        this.passwordEntryService = passwordEntryService;
    }

    @GetMapping("/entries")
    public String passwordEntryView(Model model) {
        List<PasswordEntry> entries = passwordEntryService.findAll();
        model.addAttribute("passwords", entries);
        return "password/password_entry";
    }
}
