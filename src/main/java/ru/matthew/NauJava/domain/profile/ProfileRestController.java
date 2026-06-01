package ru.matthew.NauJava.domain.profile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.matthew.NauJava.domain.profile.dto.*;
import ru.matthew.NauJava.domain.user.CustomUserDetails;

@RestController
@RequestMapping("/profiles")
public class ProfileRestController {

    private final ProfileService profileService;

    @Autowired
    public ProfileRestController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ProfileResponseDto> createProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileRequestDto dto
    ) {
        var profile = profileService.createProfile(userDetails.id(), dto);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> getProfileById(
            @PathVariable(name = "id") Long profileId
    ) {
        var profile = profileService.findById(profileId);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ProfileResponseDto> getProfileByName(
            @RequestParam String name,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        var profile = profileService.findByName(userDetails.id(), name);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProfileResponseDto>> getProfiles(
            @Valid ProfileSearchFilterDto filter,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        if (filter.createAt() != null) {
            var profiles = profileService.findAllByCreateAt(userDetails.id(), filter.createAt(), pageable);
            return new ResponseEntity<>(profiles, HttpStatus.OK);
        }
        if (filter.startDate() != null && filter.endDate() != null) {
            var profiles = profileService.findAllByCreatedAtBetween(userDetails.id(), filter.startDate(), filter.endDate(), pageable);
            return new ResponseEntity<>(profiles, HttpStatus.OK);
        }
        var profiles = profileService.findAllByUserId(userDetails.id(), pageable);
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCountAllProfilesByUserId(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        var total = profileService.countAllProfilesByUserId(userDetails.id());
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> updateSettings(
            @PathVariable(name = "id") Long profileId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileRequestDto dto
    ) {
        var profile = profileService.updateSettings(userDetails.id(), profileId, dto);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfiles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid ProfileDeleteFilterDto filter
    ) {
        if (filter.name() != null) {
            profileService.deleteByName(userDetails.id(), filter.name());
        } else if (filter.createAt() != null) {
            profileService.deleteAllByCreatedAt(userDetails.id(), filter.createAt());
        } else if (filter.startDate() != null && filter.endDate() != null) {
            profileService.deleteAllByCreatedAtBetween(userDetails.id(), filter.startDate(), filter.endDate());
        } else {
            profileService.deleteAllByUserId(userDetails.id());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable(name = "id") Long profileId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        profileService.deleteById(userDetails.id(), profileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
