package ru.matthew.NauJava.domain.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.matthew.NauJava.domain.user.UserService;

@Controller
@RequestMapping("/audit")
public class AuditController {

    private final UserService userService;
    private final AuditService auditService;

    private static final int PAGE_SIZE = 10;

    @Autowired
    public AuditController(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping
    public String auditPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        var userDto = userService.findByUsername(userDetails.getUsername());
        long total = auditService.countAllEvent();
        int safePage = clampVaultPage(total, page);
        var pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        var events = auditService.findAll(pageable);

        model.addAttribute("events", events.getContent());
        model.addAttribute("eventPage", events);
        model.addAttribute("currentPage", events.getNumber());

        model.addAttribute("userEmail", userDto.email());
        model.addAttribute("userRole", userDto.role());
        model.addAttribute("username", userDto.username());

        model.addAttribute("totalUsers", auditService.countAllUser());
        model.addAttribute("totalEntries", auditService.countAllPasswordEntries());
        model.addAttribute("newUsers24h", auditService.countAllUserForLastDay());
        model.addAttribute("topAlgorithm", auditService.getMostPopularCipher());

        return "/statistics/audit";
    }

    private static int clampVaultPage(long totalElements, int page) {
        if (totalElements <= 0) {
            return 0;
        }
        int totalPages = (int) ((totalElements + PAGE_SIZE - 1) / PAGE_SIZE);
        return Math.min(Math.max(0, page), totalPages - 1);
    }
}
