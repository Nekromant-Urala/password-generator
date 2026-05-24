package ru.matthew.NauJava.domain.password;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryRequestDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryUpdateDto;
import ru.matthew.NauJava.domain.user.UserService;

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
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        long total = passwordEntryService.countAllEntryByUserId(userDto.id());
        int safePage = clampVaultPage(total, page);
        var pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        var entries = passwordEntryService.findAllByPageForUser(userDto.id(), pageable);

        model.addAttribute("entries", entries.getContent());
        model.addAttribute("passwordPage", entries);
        model.addAttribute("currentPage", entries.getNumber());
        model.addAttribute("userEmail", userDto.email());
        model.addAttribute("userRole", userDto.role());
        model.addAttribute("username", userDto.username());

        return "/passwords/entries";
    }

    @PostMapping("/create")
    public String createEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("passwordEntryRequestDto") PasswordEntryRequestDto dto, //TODO навесить валидацию в dto
            RedirectAttributes redirectAttributes
    ) {

        var userDto = userService.findByUsername(userDetails.getUsername());
        var entry = passwordEntryService.createPasswordEntry(userDto.id(), dto);
        if (entry == null) {
            redirectAttributes.addFlashAttribute("vaultError", "Запись не создана.");
        } else {
            redirectAttributes.addFlashAttribute("vaultSuccess", "Запись создана.");
        }

        return vaultRedirect(0);
    }

    @PostMapping("/{id}/update")
    public String updateEntry(
            @PathVariable(name = "id") Long entryId,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("passwordEntryDto") PasswordEntryUpdateDto dto, //TODO навесить валидацию в dto
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        var entry = passwordEntryService.updatePatchEntry(entryId, dto);
        if (entry == null) {
            redirectAttributes.addFlashAttribute("vaultError", "Запись не найдена или недоступна.");
        } else {
            redirectAttributes.addFlashAttribute("vaultSuccess", "Запись обновлена.");
        }
        long total = passwordEntryService.countAllEntryByUserId(userDto.id());
        int safePage = clampVaultPage(total, page);
        return vaultRedirect(safePage);
    }

    @PostMapping("/{id}/delete")
    public String deleteEntry(
            @PathVariable(name = "id") Long entryId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        passwordEntryService.deleteById(userDto.id(), entryId);
        long totalAfter = passwordEntryService.countAllEntryByUserId(userDto.id());
        int safePage = clampVaultPage(totalAfter, page);
        redirectAttributes.addFlashAttribute("vaultSuccess", "Запись удалена.");
        return vaultRedirect(safePage);
    }

    @PostMapping("/{id}/reveal")
    public String revealPassword(
            @PathVariable(name = "id") Long entryId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        long totalAfter = passwordEntryService.countAllEntryByUserId(userDto.id());
        int safePage = clampVaultPage(totalAfter, page);
        var entry = passwordEntryService.revealPassword(entryId);
        if (entry == null) {
            redirectAttributes.addFlashAttribute("vaultError", "Пароль не найден или недоступен.");
            return vaultRedirect(safePage);
        }

        redirectAttributes.addFlashAttribute("revealedEntryId", entryId);
        redirectAttributes.addFlashAttribute("revealedPassword", new String(entry.pass()));
        return vaultRedirect(safePage);
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
