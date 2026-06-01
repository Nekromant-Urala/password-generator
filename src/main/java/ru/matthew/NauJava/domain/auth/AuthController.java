package ru.matthew.NauJava.domain.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.matthew.NauJava.domain.user.dto.UserCreateDto;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;

@Controller
public class AuthController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/sign-in")
    public String signIn(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("errorMessage", "Неверный пароль или логин");
        }
        return "auth/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp() {
        return "auth/sign-up";
    }

    @PostMapping("/sign-up")
    public String singUp(
            @Valid @ModelAttribute UserCreateDto user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            var errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/sign-up";
        }

        try {
            authenticationService.singUp(user);
            redirectAttributes.addFlashAttribute("success", "Регистрация успешна! Войдите в систему.");
            return "redirect:/sign-in";
        } catch (UserAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("success", "Попробуйте другое имя пользователя или почту");
            return "redirect:/sign-in";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/sign-up";
        }
    }
}
