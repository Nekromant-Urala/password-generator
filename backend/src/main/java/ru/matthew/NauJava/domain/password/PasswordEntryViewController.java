package ru.matthew.NauJava.domain.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String passwordEntryView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<PasswordEntry> entries = passwordEntryService.findByUsername(username);
        model.addAttribute("passwords", entries);
        return "user/password/entries";
    }
}
