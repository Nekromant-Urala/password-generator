package ru.matthew.NauJava.domain.auth;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.matthew.NauJava.domain.user.dto.UserCreateDto;

@Controller
public class AuthController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/sign-in")
    public String signIn() {
        return "/auth/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp() {
        return "/auth/sign-up";
    }

    @PostMapping("/sign-up")
    public String singUp(@Valid @ModelAttribute UserCreateDto user, RedirectAttributes redirectAttributes) {
        try {
            authenticationService.singUp(user);
            redirectAttributes.addFlashAttribute("success", "Регистрация успешна! Войдите в систему.");
            return "redirect:/sign-in";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/sign-up";
        }
    }
}
