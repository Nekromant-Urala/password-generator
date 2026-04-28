package ru.matthew.NauJava.domain.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.exception.UserExistsException;
import ru.matthew.NauJava.domain.user.UserService;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "user/registration";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        try {
            userService.createUser(user);
            return "redirect:/login";
        } catch (UserExistsException e) {
            model.addAttribute("message", "Пользователь уже существует");
            return "user/registration";
        }
    }
}
