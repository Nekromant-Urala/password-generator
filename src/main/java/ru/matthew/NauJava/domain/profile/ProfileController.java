package ru.matthew.NauJava.domain.profile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.matthew.NauJava.domain.profile.dto.ProfileRequestDto;
import ru.matthew.NauJava.domain.profile.exception.ProfileAlreadyExistsException;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.user.CustomUserDetails;
import ru.matthew.NauJava.domain.user.UserService;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

@Controller
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    private static final int PAGE_SIZE = 10;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String profilesPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        var total = profileService.countAllProfilesByUserId(userDetails.id());
        int safePage = clampVaultPage(total, page);
        var pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createAt")
        );
        var profiles = profileService.findAllByUserId(userDetails.id(), pageable);

        model.addAttribute("username", userDetails.username());
        model.addAttribute("userEmail", userDetails.email());
        model.addAttribute("userRole", userDetails.role());

        model.addAttribute("profilePage", profiles);
        model.addAttribute("profiles", profiles.getContent());
        model.addAttribute("currentPage", safePage);

        return "generators/profiles";
    }

    @PostMapping("/create")
    public String createProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("profileCreateDto") ProfileRequestDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            var errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            redirectAttributes.addFlashAttribute("profileError", errorMessage);
            return vaultRedirect(0);
        }
        try {
            var profile = profileService.createProfile(userDetails.id(), dto);
            redirectAttributes.addFlashAttribute("profileSuccess", "Профиль создан.");
        } catch (UserNotFoundException | ProfileAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
        }

        return vaultRedirect(0);
    }

    @PostMapping("/{id}/update")
    public String updateProfile(
            @PathVariable(name = "id") Long profileId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("profileRequestDto") ProfileRequestDto dto,
            BindingResult bindingResult,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            var errorMessage = bindingResult.getAllErrors().getFirst().getDefaultMessage();
            redirectAttributes.addFlashAttribute("profileError", errorMessage);
            return vaultRedirect(0);
        }
        try {
            var profile = profileService.updateSettings(userDetails.id(), profileId, dto);
            redirectAttributes.addFlashAttribute("profileSuccess", "Профиль обновлена.");
            long total = profileService.countAllProfilesByUserId(userDetails.id());
            int safePage = clampVaultPage(total, page);
            return vaultRedirect(safePage);
        } catch (ProfileNotFoundException e) {
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
            return vaultRedirect(0);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteProfile(
            @PathVariable(name = "id") Long profileId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        var total = profileService.countAllProfilesByUserId(userDetails.id());
        profileService.deleteById(userDetails.id(), profileId);
        int safePage = clampVaultPage(total, page);
        redirectAttributes.addFlashAttribute("profileSuccess", "Запись удалена.");
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
        return "redirect:/profiles?page=" + Math.max(0, page);
    }

}
