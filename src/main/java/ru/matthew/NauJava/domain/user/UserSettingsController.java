package ru.matthew.NauJava.domain.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.matthew.NauJava.domain.user.dto.UserPatchDto;
import ru.matthew.NauJava.domain.user.dto.UserUpdatePasswordDto;
import ru.matthew.NauJava.domain.user.exception.UserAlreadyExistsException;
import ru.matthew.NauJava.domain.user.exception.UserPasswordMissMatchException;


@Controller
@RequestMapping("/settings")
public class UserSettingsController {

    private final UserService userService;

    @Autowired
    public UserSettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String settingsPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("userEmail", userDetails.email());
        model.addAttribute("userRole", userDetails.role());

        return "user/settings";
    }

    @PostMapping("/profile")
    public String updateSettings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("userPatchDto") UserPatchDto patchDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            var errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/settings";
        }

        try {
            userService.patchUser(userDetails.id(), patchDto);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен!");
        } catch (UserAlreadyExistsException e){
            redirectAttributes.addFlashAttribute("errorMessage", "Данное имя пользователя или email уже заняты.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла неизвестная ошибка");
        }

        return "redirect:/settings";
    }

    @PostMapping("/password")
    public String changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("updatePasswordDto") UserPatchDto updatePasswordDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            var errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/settings";
        }
        try {
            userService.patchUser(userDetails.id(), updatePasswordDto);
            redirectAttributes.addFlashAttribute("successMessage", "Пароль успешно изменен!");
        } catch (UserPasswordMissMatchException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла неизвестная ошибка");
        }

        return "redirect:/settings";
    }
}
