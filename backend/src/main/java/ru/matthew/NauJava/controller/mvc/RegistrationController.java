package ru.matthew.NauJava.controller.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.matthew.NauJava.entity.User;
import ru.matthew.NauJava.repository.exception.UserExistsException;
import ru.matthew.NauJava.service.user.UserService;

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
