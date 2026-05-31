package ru.matthew.NauJava.service.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.profile.ProfileServiceImpl;
import ru.matthew.NauJava.domain.profile.dto.ProfileRequestDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileResponseDto;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.profile.mapper.ProfileMapper;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private ProfileServiceImpl profileService;

    private User user;
    private Long userId;
    private Profile profile;
    private Long profileId;
    private Profile savedProfile;
    private String nameProfile;

    private ProfileResponseDto profileResponseDto;

    private User createUser(String username, String email) {
        var oUser = new User();
        oUser.setUsername(username);
        oUser.setEmail(email);
        return oUser;
    }

    private Profile createProfile(String name) {
        return new Profile.ProfileBuilder()
                .name(name)
                .passwordLength(12)
                .uppercase(true)
                .lowercase(true)
                .digits(true)
                .specialChars(true)
                .duplicateChars(true)
                .favorite(true)
                .customChars("")
                .kdfAlgorithm(Pbkdf2Spec.PBKDF_2)
                .cipher(CipherAlgorithmSpec.AES)
                .build();
    }

    private ProfileRequestDto createRequestDto(String name) {
        return new ProfileRequestDto(
                name, 12, true, true, true,
                true, true, true, "",
                Pbkdf2Spec.PBKDF_2.getName(), CipherAlgorithmSpec.AES.getName()
        );
    }

    private ProfileResponseDto createResponseDto(Long profileId, Long userId, String name, LocalDateTime createdAt) {
        return new ProfileResponseDto(
                profileId, userId, name, 12, true, true, true,
                true, true, true, "",
                createdAt, Pbkdf2Spec.PBKDF_2.getName(), CipherAlgorithmSpec.AES.getName()
        );
    }


    @BeforeEach
    public void setUp() {
        user = createUser("username", "test@test.ru");
        userId = 11L;
        user.setId(userId);
        nameProfile = "name";

        profile = createProfile(nameProfile);
        profile.setCreateAt(LocalDateTime.now());

        savedProfile = createProfile(nameProfile);
        profileId = 1L;
        savedProfile.setId(profileId);
        savedProfile.setUser(user);

        profileResponseDto = createResponseDto(profileId, userId, nameProfile, savedProfile.getCreateAt());
    }

    @Test
    public void createProfile_SuccessCreateProfile_ReturnProfileResponseDto() {
        var profileRequestDto = createRequestDto(nameProfile);

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(profileMapper.toProfile(profileRequestDto)).thenReturn(profile);
        when(profileRepository.save(profile)).thenReturn(savedProfile);
        when(profileMapper.toProfileResponseDto(savedProfile)).thenReturn(profileResponseDto);

        var createdProfile = profileService.createProfile(userId, profileRequestDto);

        Assertions.assertNotNull(createdProfile);
        Assertions.assertEquals(profileId, createdProfile.id());
        Assertions.assertEquals(userId, createdProfile.userId());

        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void createProfile_WhenUserNotFound_ReturnUserNotFoundException() {
        var profileRequestDto = createRequestDto(nameProfile);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> profileService.createProfile(userId, profileRequestDto));

        verify(profileMapper, Mockito.never()).toProfile(profileRequestDto);
        verify(profileRepository, Mockito.never()).save(profile);
        verify(profileMapper, Mockito.never()).toProfileResponseDto(savedProfile);
        verify(eventPublisher, Mockito.never()).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void createDefaultProfile_SuccessCreate() {
        ArgumentCaptor<Profile> profileArgumentCaptor = ArgumentCaptor.forClass(Profile.class);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        profileService.createDefaultProfile(userId);

        verify(profileRepository).save(profileArgumentCaptor.capture());
        var captureProfile = profileArgumentCaptor.getValue();
        Assertions.assertEquals("default", captureProfile.getName());
        Assertions.assertEquals(12, captureProfile.getPasswordLength());
        Assertions.assertEquals(Pbkdf2Spec.PBKDF_2, captureProfile.getKdfAlgorithm());
        Assertions.assertEquals(CipherAlgorithmSpec.AES, captureProfile.getCipher());
    }

    @Test
    public void createDefaultProfile_WhenUserNotFound_ReturnUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> profileService.createDefaultProfile(userId));

        verify(profileRepository, Mockito.never()).save(any(Profile.class));
    }

    @Test
    public void findById_WhenProfileExists_ReturnProfileResponseDto() {
        when(profileRepository.findById(profileId)).thenReturn(Optional.ofNullable(savedProfile));
        when(profileMapper.toProfileResponseDto(savedProfile)).thenReturn(profileResponseDto);

        var foundProfile = profileService.findById(profileId);

        Assertions.assertNotNull(foundProfile);
        Assertions.assertEquals(profileId, foundProfile.id());
        Assertions.assertEquals(nameProfile, foundProfile.name());
        Assertions.assertEquals(userId, foundProfile.userId());
    }

    @Test
    public void findById_WhenProfileDoesNotExists_ReturnProfileNotFoundException() {
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProfileNotFoundException.class, () -> profileService.findById(profileId));

        verify(profileMapper, Mockito.never()).toProfileResponseDto(savedProfile);
    }

    @Test
    public void findByName_WhenProfileExists_ReturnProfileResponseDto() {
        when(profileRepository.findByUserIdAndName(userId, nameProfile)).thenReturn(Optional.ofNullable(savedProfile));
        when(profileMapper.toProfileResponseDto(savedProfile)).thenReturn(profileResponseDto);

        var foundProfile = profileService.findByName(userId, nameProfile);

        Assertions.assertNotNull(foundProfile);
        Assertions.assertEquals(profileId, foundProfile.id());
        Assertions.assertEquals(nameProfile, foundProfile.name());
        Assertions.assertEquals(userId, foundProfile.userId());
    }

    @Test
    public void findByName_WhenProfileDoesNotExists_ReturnProfileNotFoundException() {
        when(profileRepository.findByUserIdAndName(userId, nameProfile)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProfileNotFoundException.class, () -> profileService.findByName(userId, nameProfile));

        verify(profileMapper, Mockito.never()).toProfileResponseDto(savedProfile);
    }

    @Test
    public void findAllByCreatedAtBetween_WhenProfileExists_ReturnPaginatedProfileResponseDto() {
        var profile1 = createProfile("name1");
        var profile2 = createProfile("name2");

        profile1.setId(1L);
        profile2.setId(2L);
        profile1.setCreateAt(LocalDateTime.now());
        profile2.setCreateAt(LocalDateTime.now());
        profile1.setUser(user);
        profile2.setUser(user);

        var oDto1 = createResponseDto(profile1.getId(), userId, profile1.getName(), profile1.getCreateAt());
        var oDto2 = createResponseDto(profile2.getId(), userId, profile2.getName(), profile2.getCreateAt());

        var startDate = LocalDateTime.now().minusHours(1);
        var endDate = LocalDateTime.now().plusHours(1);

        List<Profile> profiles = List.of(profile1, profile2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Profile> profilePage = new PageImpl<>(profiles, pageable, profiles.size());
        when(profileRepository.findAllByUserIdAndCreateAtBetween(userId, startDate, endDate, pageable)).thenReturn(profilePage);
        when(profileMapper.toProfileResponseDto(profile1)).thenReturn(oDto1);
        when(profileMapper.toProfileResponseDto(profile2)).thenReturn(oDto2);

        var foundProfilesPage = profileService.findAllByCreatedAtBetween(userId, startDate, endDate, pageable);

        Assertions.assertNotNull(foundProfilesPage);
        Assertions.assertEquals(2, foundProfilesPage.getTotalElements());
        Assertions.assertEquals(1, foundProfilesPage.getTotalPages());
        Assertions.assertEquals(2, foundProfilesPage.getNumberOfElements());

        var foundProfiles = foundProfilesPage.getContent().stream().map(ProfileResponseDto::id).toList();
        Assertions.assertEquals(List.of(1L, 2L), foundProfiles);
    }

    @Test
    public void findAllByCreatedAtBetween_WhenProfileDoesNotExists_ReturnEmptyPage() {
        var startDate = LocalDateTime.now().minusHours(1);
        var endDate = LocalDateTime.now().plusHours(1);

        List<Profile> profiles = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Profile> profilePage = new PageImpl<>(profiles, pageable, 0);
        when(profileRepository.findAllByUserIdAndCreateAtBetween(userId, startDate, endDate, pageable)).thenReturn(profilePage);

        var foundProfilesPage = profileService.findAllByCreatedAtBetween(userId, startDate, endDate, pageable);

        Assertions.assertNotNull(foundProfilesPage);
        Assertions.assertEquals(0, foundProfilesPage.getTotalElements());
        Assertions.assertEquals(0, foundProfilesPage.getTotalPages());
        Assertions.assertEquals(0, foundProfilesPage.getNumberOfElements());

        verify(profileMapper, Mockito.never()).toProfileResponseDto(any(Profile.class));
    }

    @Test
    public void findAllByCreateAt_WhenProfileExists_ReturnPaginatedProfileResponseDto() {
        var profile1 = createProfile("name1");

        var createdDate = LocalDateTime.now().minusHours(1);

        profile1.setId(1L);
        profile1.setCreateAt(createdDate);
        profile1.setUser(user);

        var oDto1 = createResponseDto(profile1.getId(), userId, profile1.getName(), profile1.getCreateAt());

        List<Profile> profiles = List.of(profile1);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Profile> profilePage = new PageImpl<>(profiles, pageable, profiles.size());
        when(profileRepository.findAllByUserIdAndCreateAt(userId, createdDate, pageable)).thenReturn(profilePage);
        when(profileMapper.toProfileResponseDto(profile1)).thenReturn(oDto1);

        var foundProfile = profileService.findAllByCreateAt(userId, createdDate, pageable);

        Assertions.assertNotNull(foundProfile);
        Assertions.assertEquals(1, foundProfile.getTotalElements());
        Assertions.assertEquals(1, foundProfile.getTotalPages());
        Assertions.assertEquals(1, foundProfile.getNumberOfElements());

        Assertions.assertEquals(1L, foundProfile.getContent().getFirst().id());
    }

    @Test
    public void findAllByCreateAt_WhenProfileDoesNotExists_ReturnPaginatedProfileResponseDto() {
        var createdDate = LocalDateTime.now().minusHours(1);

        List<Profile> profiles = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Profile> profilePage = new PageImpl<>(profiles, pageable, 0);
        when(profileRepository.findAllByUserIdAndCreateAt(userId, createdDate, pageable)).thenReturn(profilePage);

        var foundProfile = profileService.findAllByCreateAt(userId, createdDate, pageable);

        Assertions.assertNotNull(foundProfile);
        Assertions.assertEquals(0, foundProfile.getTotalElements());
        Assertions.assertEquals(0, foundProfile.getTotalPages());
        Assertions.assertEquals(0, foundProfile.getNumberOfElements());

        verify(profileMapper, Mockito.never()).toProfileResponseDto(any(Profile.class));
    }

    @Test
    public void findAllByUserId_WhenProfileExists_ReturnPaginatedProfileResponseDto() {
        var profile1 = createProfile("name1");
        var profile2 = createProfile("name2");

        profile1.setId(1L);
        profile2.setId(2L);
        profile1.setCreateAt(LocalDateTime.now());
        profile2.setCreateAt(LocalDateTime.now());
        profile1.setUser(user);
        profile2.setUser(user);

        var oDto1 = createResponseDto(profile1.getId(), userId, profile1.getName(), profile1.getCreateAt());
        var oDto2 = createResponseDto(profile2.getId(), userId, profile2.getName(), profile2.getCreateAt());

        List<Profile> profiles = List.of(profile1, profile2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Profile> profilePage = new PageImpl<>(profiles, pageable, profiles.size());
        when(profileRepository.findAllByUserId(userId, pageable)).thenReturn(profilePage);
        when(profileMapper.toProfileResponseDto(profile1)).thenReturn(oDto1);
        when(profileMapper.toProfileResponseDto(profile2)).thenReturn(oDto2);

        var foundProfilesPage = profileService.findAllByUserId(userId, pageable);

        Assertions.assertNotNull(foundProfilesPage);
        Assertions.assertEquals(2, foundProfilesPage.getTotalElements());
        Assertions.assertEquals(1, foundProfilesPage.getTotalPages());
        Assertions.assertEquals(2, foundProfilesPage.getNumberOfElements());

        var foundProfiles = foundProfilesPage.getContent().stream().map(ProfileResponseDto::id).toList();
        Assertions.assertEquals(List.of(1L, 2L), foundProfiles);
    }

    @Test
    public void findAllByUserId_WhenProfileDoesNotExists_ReturnEmptyPaginatedProfileResponseDto() {
        List<Profile> profiles = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Profile> profilePage = new PageImpl<>(profiles, pageable, 0);
        when(profileRepository.findAllByUserId(userId, pageable)).thenReturn(profilePage);

        var foundProfilesPage = profileService.findAllByUserId(userId, pageable);

        Assertions.assertNotNull(foundProfilesPage);
        Assertions.assertEquals(0, foundProfilesPage.getTotalElements());
        Assertions.assertEquals(0, foundProfilesPage.getTotalPages());
        Assertions.assertEquals(0, foundProfilesPage.getNumberOfElements());

        verify(profileMapper, Mockito.never()).toProfileResponseDto(any(Profile.class));
    }

    @Test
    public void findAll_WhenProfileExists_ReturnListProfileResponseDto() {
        var profile1 = createProfile("name1");
        var profile2 = createProfile("name2");

        profile1.setId(1L);
        profile2.setId(2L);
        profile1.setCreateAt(LocalDateTime.now());
        profile2.setCreateAt(LocalDateTime.now());
        profile1.setUser(user);
        profile2.setUser(user);

        var oDto1 = createResponseDto(profile1.getId(), userId, profile1.getName(), profile1.getCreateAt());
        var oDto2 = createResponseDto(profile2.getId(), userId, profile2.getName(), profile2.getCreateAt());

        List<Profile> profiles = List.of(profile1, profile2);

        when(profileRepository.findAll()).thenReturn(profiles);
        when(profileMapper.toProfileResponseDto(profile1)).thenReturn(oDto1);
        when(profileMapper.toProfileResponseDto(profile2)).thenReturn(oDto2);

        var foundProfiles = profileService.findAll();

        Assertions.assertNotNull(foundProfiles);
        Assertions.assertEquals(2, foundProfiles.size());

        Assertions.assertEquals(profile1.getId(), foundProfiles.getFirst().id());
        Assertions.assertEquals(userId, foundProfiles.getFirst().userId());

        Assertions.assertEquals(profile2.getId(), foundProfiles.getLast().id());
        Assertions.assertEquals(userId, foundProfiles.getLast().userId());
    }

    @Test
    public void findAll_WhenProfileDoesNotExists_ReturnEmptyList() {
        List<Profile> profiles = List.of();
        when(profileRepository.findAll()).thenReturn(profiles);

        var foundProfiles = profileService.findAll();

        Assertions.assertNotNull(foundProfiles);
        Assertions.assertTrue(foundProfiles.isEmpty());

        verify(profileMapper, Mockito.never()).toProfileResponseDto(any(Profile.class));
    }

    @Test
    public void countAllProfilesByUserId_ReturnNumberOfProfiles() {
        Long numberOfProfiles = 10L;
        when(profileRepository.countAllByUserId(userId)).thenReturn(numberOfProfiles);

        var count = profileService.countAllProfilesByUserId(userId);

        Assertions.assertEquals(numberOfProfiles, count);
    }

    @Test
    public void updateSettings_SuccessUpdate_ReturnProfileResponseDto() {
        var updatedName = "newProfileName";
        var update = createRequestDto(updatedName);

        var expectedDto = createResponseDto(profileId, userId, updatedName, savedProfile.getCreateAt());

        when(profileRepository.findById(profileId)).thenReturn(Optional.ofNullable(savedProfile));
        when(profileMapper.toProfileResponseDto(savedProfile)).thenReturn(expectedDto);

        var updatedProfile = profileService.updateSettings(userId, profileId, update);

        Assertions.assertNotNull(updatedProfile);
        Assertions.assertEquals(updatedName, updatedProfile.name());
        Assertions.assertEquals(profileId, updatedProfile.id());

        verify(profileMapper).updateProfileDto(savedProfile, update);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteAllByUserId_ShouldDeleteTargetProfiles() {
        profileService.deleteAllByUserId(userId);

        verify(profileRepository).deleteAllByUserId(userId);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteAllByCreatedAt_ShouldDeleteTargetProfiles() {
        var createdAt = LocalDateTime.now().minusHours(1);

        profileService.deleteAllByCreatedAt(userId, createdAt);

        verify(profileRepository).deleteAllByUserIdAndCreateAt(userId, createdAt);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteAllByCreatedAtBetween_ShouldDeleteTargetProfiles() {
        var startDate = LocalDateTime.now().minusHours(1);
        var endDate = LocalDateTime.now().plusHours(1);

        profileService.deleteAllByCreatedAtBetween(userId, startDate, endDate);

        verify(profileRepository).deleteAllByUserIdAndCreateAtBetween(userId, startDate, endDate);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteByName_ShouldDeleteTargetProfiles() {
        var nameForDelete = "name";

        profileService.deleteByName(userId, nameForDelete);

        verify(profileRepository).deleteByUserIdAndName(userId, nameForDelete);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteById_ShouldDeleteTargetProfiles() {
        profileService.deleteById(userId, profileId);

        verify(profileRepository).deleteById(profileId);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }
}
