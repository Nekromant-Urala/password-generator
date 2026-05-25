package ru.matthew.NauJava.domain.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.matthew.NauJava.domain.profile.dto.ProfileRequestDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileUpdateDto;
import ru.matthew.NauJava.domain.user.UserService;

@Controller
@RequestMapping("/profiles")
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;

    private static final int PAGE_SIZE = 10;

    @Autowired
    public ProfileController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping
    public String profilesPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        var total = profileService.countAllProfilesByUserId(userDto.id());
        int safePage = clampVaultPage(total, page);
        var pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createAt")
        );
        var profiles = profileService.findAllByUserId(userDto.id(), pageable);

        model.addAttribute("username", userDto.username());
        model.addAttribute("userEmail", userDto.email());
        model.addAttribute("userRole", userDto.role());

        model.addAttribute("profilePage", profiles);
        model.addAttribute("profiles", profiles.getContent());
        model.addAttribute("currentPage", safePage);

        return "/generators/profiles";
    }

    @PostMapping("/create")
    public String createProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("profileCreateDto") ProfileRequestDto dto,
            RedirectAttributes redirectAttributes
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        var profile = profileService.createProfile(userDto.id(), dto);
        if (profile == null) {
            redirectAttributes.addFlashAttribute("profileError", "Профиль не создан.");
        } else {
            redirectAttributes.addFlashAttribute("profileSuccess", "Профиль создан.");
        }

        return vaultRedirect(0);
    }

    @PostMapping("/{id}/update")
    public String updateProfile(
            @PathVariable(name = "id") Long profileId,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("profileUpdateDto") ProfileUpdateDto dto,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        var profile = profileService.updateSettings(profileId, dto);
        if (profile == null) {
            redirectAttributes.addFlashAttribute("profileError", "Профиль не обновлена.");
        } else {
            redirectAttributes.addFlashAttribute("profileSuccess", "Профиль обновлена.");
        }
        long total = profileService.countAllProfilesByUserId(userDto.id());
        int safePage = clampVaultPage(total, page);
        return vaultRedirect(safePage);
    }

    @PostMapping("/{id}/delete")
    public String deleteProfile(
            @PathVariable(name = "id") Long profileId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            RedirectAttributes redirectAttributes
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        var total = profileService.countAllProfilesByUserId(userDto.id());
        profileService.deleteById(userDto.id(), profileId);
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
