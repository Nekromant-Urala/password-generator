package ru.matthew.NauJava.domain.password;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryRequestDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryUpdateDto;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.user.CustomUserDetails;
import ru.matthew.NauJava.domain.user.UserService;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

@Controller
@RequestMapping("/passwords")
public class PasswordEntryController {

    private final UserService userService;
    private final PasswordEntryService passwordEntryService;

    private static final int PAGE_SIZE = 10;

    public PasswordEntryController(UserService userService, PasswordEntryService passwordEntryService) {
        this.userService = userService;
        this.passwordEntryService = passwordEntryService;
    }

    @GetMapping
    public String passwordPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        long total = passwordEntryService.countAllEntryByUserId(userDetails.id());
        int safePage = clampVaultPage(total, page);
        var pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        var entries = passwordEntryService.findAllByPageForUser(userDetails.id(), pageable);

        model.addAttribute("entries", entries.getContent());
        model.addAttribute("passwordPage", entries);
        model.addAttribute("currentPage", entries.getNumber());
        model.addAttribute("userEmail", userDetails.email());
        model.addAttribute("userRole", userDetails.role());
        model.addAttribute("username", userDetails.username());

        return "passwords/entries";
    }

    @PostMapping("/create")
    public String createEntry(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("passwordEntryRequestDto") PasswordEntryRequestDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            var errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            redirectAttributes.addFlashAttribute("vaultError", errorMessage);
            return vaultRedirect(0);
        }

        try {
            passwordEntryService.createPasswordEntry(userDetails.id(), dto);
            redirectAttributes.addFlashAttribute("vaultSuccess", "Запись создана.");
        } catch (UserNotFoundException | ProfileNotFoundException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("vaultError", e.getMessage());
            return vaultRedirect(0);
        } catch (EncryptionException e) {
            redirectAttributes.addFlashAttribute("vaultError", "Ошибка при шифровании пароля");
            return vaultRedirect(0);
        }

        return vaultRedirect(0);
    }

    @PostMapping("/{id}/update")
    public String updateEntry(
            @PathVariable(name = "id") Long entryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("passwordEntryDto") PasswordEntryUpdateDto dto,
            BindingResult bindingResult,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            var errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            redirectAttributes.addFlashAttribute("vaultError", errorMessage);
            return vaultRedirect(0);
        }
        try {
            var entry = passwordEntryService.updatePatchEntry(entryId, dto);
            redirectAttributes.addFlashAttribute("vaultSuccess", "Запись обновлена.");
        } catch (PasswordEntryNotFoundException e) {
            redirectAttributes.addFlashAttribute("vaultError", e.getMessage());
            return vaultRedirect(0);
        } catch (EncryptionException e) {
            redirectAttributes.addFlashAttribute("vaultError", "Ошибка при шифровании пароля");
            return vaultRedirect(0);
        }

        long total = passwordEntryService.countAllEntryByUserId(userDetails.id());
        int safePage = clampVaultPage(total, page);
        return vaultRedirect(safePage);
    }

    @PostMapping("/{id}/delete")
    public String deleteEntry(
            @PathVariable(name = "id") Long entryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        passwordEntryService.deleteById(userDetails.id(), entryId);
        long totalAfter = passwordEntryService.countAllEntryByUserId(userDetails.id());
        int safePage = clampVaultPage(totalAfter, page);
        redirectAttributes.addFlashAttribute("vaultSuccess", "Запись удалена.");
        return vaultRedirect(safePage);
    }

    @PostMapping("/{id}/reveal")
    public String revealPassword(
            @PathVariable(name = "id") Long entryId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        long totalAfter = passwordEntryService.countAllEntryByUserId(userDetails.id());
        int safePage = clampVaultPage(totalAfter, page);

        try {
            var entry = passwordEntryService.revealPassword(entryId);
            redirectAttributes.addFlashAttribute("revealedEntryId", entryId);
            redirectAttributes.addFlashAttribute("revealedPassword", new String(entry.pass()));
            return vaultRedirect(safePage);
        } catch (PasswordEntryNotFoundException e) {
            redirectAttributes.addFlashAttribute("vaultError", "Пароль не найден или недоступен.");
            return vaultRedirect(safePage);
        } catch (EncryptionException e) {
            redirectAttributes.addFlashAttribute("vaultError", "Ошибка при расшифровывании пароля");
            return vaultRedirect(safePage);
        }
    }

    private static int clampVaultPage(long totalElements, int page) {
        if (totalElements <= 0) {
            return 0;
        }
        int totalPages = (int) ((totalElements + PAGE_SIZE - 1) / PAGE_SIZE);
        return Math.min(Math.max(0, page), totalPages - 1);
    }

    private static String vaultRedirect(int page) {
        return "redirect:/passwords?page=" + Math.max(0, page);
    }
}
