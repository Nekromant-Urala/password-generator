package ru.matthew.NauJava.service.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionService;
import ru.matthew.NauJava.domain.crypto.generation.RandomGeneratorService;
import ru.matthew.NauJava.domain.password.PasswordEntry;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.password.PasswordEntryServiceImpl;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryRequestDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.password.mapper.PasswordEntryMapper;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.profile.dto.ProfileForPasswordDto;
import ru.matthew.NauJava.domain.profile.mapper.ProfileMapper;
import ru.matthew.NauJava.domain.user.Role;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.matthew.NauJava.domain.user.Role.USER;

@ExtendWith(MockitoExtension.class)
public class PasswordEntryServiceTest {

    @Mock
    private PasswordEntryMapper passwordEntryMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEntryRepository passwordEntryRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private RandomGeneratorService generatorService;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private PasswordEntryServiceImpl passwordEntryService;

    private User user;
    private Long userId;
    private Long passwordEntryId;
    private String service = "service";
    private String login = "login";
    private PasswordEntry passwordEntry;
    private PasswordEntry savedPasswordEntry;

    private PasswordEntryResponseDto passwordEntryResponseDto;

    private User createUser(String username, String email) {
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hash_password");
        user.setRole(USER);
        return user;
    }

    private PasswordEntry createPasswordEntry(String service, String login) {
        var entry = new PasswordEntry();
        entry.setServiceName(service);
        entry.setLogin(login);
        entry.setPassword("pass");
        entry.setDescription("desc");
        return entry;
    }

    private PasswordEntryResponseDto createPasswordEntryResponseDto(PasswordEntry entry) {
        return new PasswordEntryResponseDto(
                entry.getId(), login, entry.getPassword().toCharArray(), service, entry.getDescription(), entry.getCreatedAt(), entry.getUpdatedAt()
        );
    }

    private Profile createProfile(String name) {
        Profile profile = new Profile.ProfileBuilder()
                .name(name)
                .cipher(CipherAlgorithmSpec.AES)
                .kdfAlgorithm(Pbkdf2Spec.PBKDF_2)
                .build();
        profile.setId(1L);
        return profile;
    }

    @BeforeEach
    public void setUp() {
        user = createUser("username", "test@test.ru");
        userId = 11L;
        user.setId(userId);

        passwordEntry = createPasswordEntry(service, login);
        passwordEntry.setCreatedAt(LocalDateTime.now());
        passwordEntry.setUpdatedAt(LocalDateTime.now());

        savedPasswordEntry = createPasswordEntry(service, login);
        passwordEntryId = 12L;
        savedPasswordEntry.setId(passwordEntryId);
        savedPasswordEntry.setUser(user);
        savedPasswordEntry.setCreatedAt(passwordEntry.getCreatedAt());
        savedPasswordEntry.setUpdatedAt(passwordEntry.getUpdatedAt());

        passwordEntryResponseDto = createPasswordEntryResponseDto(savedPasswordEntry);
    }

    @Test
    public void createPasswordEntry_Success_ReturnPasswordEntryResponseDto() {
        var profileName = "default";
        var requestDto = new PasswordEntryRequestDto(
                "login", "pass".toCharArray(), "service", "desc", profileName
        );
        var profileForPasswordDto = new ProfileForPasswordDto(12, true, true, true, true, true, "");
        var profile = createProfile(profileName);
        user.addProfile(profile);
        profile.setUser(user);

        when(passwordEntryMapper.toPasswordEntry(requestDto)).thenReturn(passwordEntry);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(profileRepository.findByUserIdAndName(userId, profileName)).thenReturn(Optional.of(profile));

        when(encryptionService.encrypt(any(), any(), any(), any())).thenReturn(new byte[]{1, 2, 3, 4, 5});
        when(passwordEntryRepository.save(passwordEntry)).thenReturn(savedPasswordEntry);
        when(passwordEntryMapper.toPasswordEntryResponseDto(savedPasswordEntry)).thenReturn(passwordEntryResponseDto);

        var createdEntry = passwordEntryService.createPasswordEntry(userId, requestDto);

        Assertions.assertNotNull(createdEntry);
        Assertions.assertEquals(passwordEntryId, createdEntry.id());
        Assertions.assertEquals(service, createdEntry.serviceName());

        verify(eventPublisher).publishEvent(any(AuditEventDto.class));

        verify(generatorService, Mockito.never()).generatePassword(profileForPasswordDto);
        verify(profileRepository, Mockito.never()).findByUserIdAndIsFavoriteTrue(userId);
    }

    @Test
    public void createPasswordEntry_WithEmptyPassword_ShouldGeneratePassword() {
        var profileName = "default";
        var requestDto = new PasswordEntryRequestDto(
                "login", new char[0], "service", "desc", profileName
        );
        var profileForPasswordDto = new ProfileForPasswordDto(12, true, true, true, true, true, "");
        var profile = createProfile(profileName);
        user.addProfile(profile);
        profile.setUser(user);

        when(passwordEntryMapper.toPasswordEntry(requestDto)).thenReturn(passwordEntry);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(profileRepository.findByUserIdAndName(userId, profile.getName())).thenReturn(Optional.of(profile));
        when(profileMapper.toProfileForPasswordDto(profile)).thenReturn(profileForPasswordDto);
        when(generatorService.generatePassword(profileForPasswordDto)).thenReturn("generatedPass".toCharArray());

        when(encryptionService.encrypt(any(), any(), any(), any())).thenReturn(new byte[]{1, 2, 3, 4, 5});
        when(passwordEntryRepository.save(passwordEntry)).thenReturn(savedPasswordEntry);
        when(passwordEntryMapper.toPasswordEntryResponseDto(savedPasswordEntry)).thenReturn(passwordEntryResponseDto);

        var createdEntry = passwordEntryService.createPasswordEntry(userId, requestDto);

        Assertions.assertNotNull(createdEntry);
        Assertions.assertEquals(passwordEntryId, createdEntry.id());
        Assertions.assertEquals(service, createdEntry.serviceName());

        verify(generatorService).generatePassword(any());
        verify(passwordEntryRepository).save(passwordEntry);
        verify(profileRepository, Mockito.never()).findByUserIdAndIsFavoriteTrue(userId);
    }

    @Test
    public void findById_WhenEntryExists_ReturnPasswordEntryResponseDto() {
        when(passwordEntryRepository.findById(passwordEntryId)).thenReturn(Optional.of(savedPasswordEntry));
        when(passwordEntryMapper.toPasswordEntryResponseDto(savedPasswordEntry)).thenReturn(passwordEntryResponseDto);

        var foundEntry = passwordEntryService.findById(passwordEntryId);

        Assertions.assertNotNull(foundEntry);
        Assertions.assertEquals(passwordEntryId, foundEntry.id());
        Assertions.assertEquals(login, foundEntry.login());
    }

    @Test
    public void findById_WhenEntryDoesNotExists_ReturnPasswordEntryNotFoundException() {
        when(passwordEntryRepository.findById(passwordEntryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException.class,
                () -> passwordEntryService.findById(passwordEntryId));

        verify(passwordEntryMapper, Mockito.never()).toPasswordEntryResponseDto(any());
    }

    @Test
    public void findByServiceName_ReturnPaginatedEntries() {
        List<PasswordEntry> entries = List.of(savedPasswordEntry);
        Pageable pageable = PageRequest.of(0, 2);
        Page<PasswordEntry> entryPage = new PageImpl<>(entries, pageable, entries.size());

        when(passwordEntryRepository.findAllByUserIdAndServiceName(userId, service, pageable)).thenReturn(entryPage);
        when(passwordEntryMapper.toPasswordEntryResponseDto(savedPasswordEntry)).thenReturn(passwordEntryResponseDto);

        var foundPage = passwordEntryService.findByServiceName(userId, service, pageable);

        Assertions.assertNotNull(foundPage);
        Assertions.assertEquals(1, foundPage.getTotalElements());
        Assertions.assertEquals(passwordEntryId, foundPage.getContent().getFirst().id());
    }

    @Test
    public void findByCreatedAtBetween_ReturnPaginatedEntries() {
        var startDate = LocalDateTime.now().minusHours(1);
        var endDate = LocalDateTime.now().plusHours(1);
        List<PasswordEntry> entries = List.of(savedPasswordEntry);
        Pageable pageable = PageRequest.of(0, 2);
        Page<PasswordEntry> entryPage = new PageImpl<>(entries, pageable, entries.size());

        when(passwordEntryRepository.findAllByUserIdAndCreatedAtBetween(userId, startDate, endDate, pageable)).thenReturn(entryPage);
        when(passwordEntryMapper.toPasswordEntryResponseDto(savedPasswordEntry)).thenReturn(passwordEntryResponseDto);

        var foundPage = passwordEntryService.findByCreatedAtBetween(userId, startDate, endDate, pageable);

        Assertions.assertNotNull(foundPage);
        Assertions.assertEquals(1, foundPage.getTotalElements());
    }

    @Test
    public void findByCreatedAt_ReturnPaginatedEntries() {
        var createdAt = savedPasswordEntry.getCreatedAt();
        List<PasswordEntry> entries = List.of(savedPasswordEntry);
        Pageable pageable = PageRequest.of(0, 2);
        Page<PasswordEntry> entryPage = new PageImpl<>(entries, pageable, entries.size());

        when(passwordEntryRepository.findByUserIdAndCreatedAt(userId, createdAt, pageable)).thenReturn(entryPage);
        when(passwordEntryMapper.toPasswordEntryResponseDto(savedPasswordEntry)).thenReturn(passwordEntryResponseDto);

        var foundPage = passwordEntryService.findByCreatedAt(userId, createdAt, pageable);

        Assertions.assertNotNull(foundPage);
        Assertions.assertEquals(1, foundPage.getTotalElements());
    }

    @Test
    public void findByUpdatedAt_ReturnPaginatedEntries() {
        var updatedAt = savedPasswordEntry.getUpdatedAt();
        List<PasswordEntry> entries = List.of(savedPasswordEntry);
        Pageable pageable = PageRequest.of(0, 2);
        Page<PasswordEntry> entryPage = new PageImpl<>(entries, pageable, entries.size());

        when(passwordEntryRepository.findByUserIdAndUpdatedAt(userId, updatedAt, pageable)).thenReturn(entryPage);
        when(passwordEntryMapper.toPasswordEntryResponseDto(savedPasswordEntry)).thenReturn(passwordEntryResponseDto);

        var foundPage = passwordEntryService.findByUpdatedAt(userId, updatedAt, pageable);

        Assertions.assertNotNull(foundPage);
        Assertions.assertEquals(1, foundPage.getTotalElements());
    }

    @Test
    public void revealPassword_Success_ReturnPasswordResponseDto() {
        user.setPassword("userPassword");
        var profile = createProfile("default");
        savedPasswordEntry.setProfile(profile);
        savedPasswordEntry.setPassword("encryptedBytesAsString");

        when(passwordEntryRepository.findById(passwordEntryId)).thenReturn(Optional.of(savedPasswordEntry));
        when(encryptionService.decrypt(any(), any(), any(), any())).thenReturn("decryptedPass".getBytes());

        var response = passwordEntryService.revealPassword(passwordEntryId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("decryptedPass", new String(response.pass()));
    }

    @Test
    public void updatePatchEntry_Success_ReturnUpdatedPasswordEntryResponseDto() throws Exception {
        var updateDto = new ru.matthew.NauJava.domain.password.dto.PasswordEntryUpdateDto(
                "newService", "newLogin", new char[]{'n', 'e', 'w'}, "newDesc"
        );

        var profile = createProfile("default");
        savedPasswordEntry.setProfile(profile);
        user.setPassword("userPassword");

        var newEntry = createPasswordEntry("newService", "newLogin");
        newEntry.setId(passwordEntryId);
        newEntry.setUser(user);
        newEntry.setProfile(profile);

        var expectedResponseDto = new PasswordEntryResponseDto(
                passwordEntryId, "newLogin", null, "newService", "newDesc", LocalDateTime.now(), LocalDateTime.now()
        );

        when(passwordEntryRepository.findById(passwordEntryId)).thenReturn(Optional.of(savedPasswordEntry));
        when(passwordEntryMapper.toPasswordEntry(savedPasswordEntry, updateDto)).thenReturn(newEntry);
        when(encryptionService.encrypt(any(), any(), any(), any())).thenReturn(new byte[]{4, 5, 6});
        when(passwordEntryMapper.toPasswordEntryResponseDto(newEntry)).thenReturn(expectedResponseDto);

        var updatedEntry = passwordEntryService.updatePatchEntry(passwordEntryId, updateDto);

        Assertions.assertNotNull(updatedEntry);
        Assertions.assertEquals("newService", updatedEntry.serviceName());
        Assertions.assertEquals("newLogin", updatedEntry.login());

        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void findAllByPageForUser_WhenEntriesExists_ReturnPaginatedEntries() {
        var entry1 = createPasswordEntry("service", "login");
        var entry2 = createPasswordEntry("service", "login");

        entry1.setId(1L);
        entry2.setId(2L);
        entry1.setUser(user);
        entry2.setUser(user);
        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);

        var eDto1 = createPasswordEntryResponseDto(entry1);
        var eDto2 = createPasswordEntryResponseDto(entry2);

        List<PasswordEntry> entries = List.of(entry1, entry2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<PasswordEntry> entryPage = new PageImpl<>(entries, pageable, entries.size());
        when(passwordEntryRepository.findAllByUserId(userId, pageable)).thenReturn(entryPage);
        when(passwordEntryMapper.toPasswordEntryResponseDto(entry1)).thenReturn(eDto1);
        when(passwordEntryMapper.toPasswordEntryResponseDto(entry2)).thenReturn(eDto2);

        var foundEntriesPage = passwordEntryService.findAllByPageForUser(userId, pageable);

        Assertions.assertNotNull(foundEntriesPage);
        Assertions.assertEquals(2, foundEntriesPage.getTotalElements());
        Assertions.assertEquals(1, foundEntriesPage.getTotalPages());
        Assertions.assertEquals(2, foundEntriesPage.getNumberOfElements());

        var foundEntries = foundEntriesPage.getContent().stream().map(PasswordEntryResponseDto::id).toList();
        Assertions.assertEquals(List.of(1L, 2L), foundEntries);
    }

    @Test
    public void findAllByPageForUser_WhenEntriesDoesNotExists_ReturnPaginatedEntries() {
        List<PasswordEntry> entries = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<PasswordEntry> entryPage = new PageImpl<>(entries, pageable, 0);
        when(passwordEntryRepository.findAllByUserId(userId, pageable)).thenReturn(entryPage);

        var foundEntriesPage = passwordEntryService.findAllByPageForUser(userId, pageable);


        Assertions.assertNotNull(foundEntriesPage);
        Assertions.assertEquals(0, foundEntriesPage.getTotalElements());
        Assertions.assertEquals(0, foundEntriesPage.getTotalPages());
        Assertions.assertEquals(0, foundEntriesPage.getNumberOfElements());

        verify(passwordEntryMapper, Mockito.never()).toPasswordEntryResponseDto(any(PasswordEntry.class));
    }

    @Test
    public void findAll_WhenEntriesExists_ReturnListPasswordEntryResponseDto() {
        var entry1 = createPasswordEntry("service", "login");
        var entry2 = createPasswordEntry("service", "login");

        entry1.setId(1L);
        entry2.setId(2L);
        entry1.setUser(user);
        entry2.setUser(user);
        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);

        var eDto1 = createPasswordEntryResponseDto(entry1);
        var eDto2 = createPasswordEntryResponseDto(entry2);

        List<PasswordEntry> entries = List.of(entry1, entry2);
        when(passwordEntryRepository.findAll()).thenReturn(entries);
        when(passwordEntryMapper.toPasswordEntryResponseDto(entry1)).thenReturn(eDto1);
        when(passwordEntryMapper.toPasswordEntryResponseDto(entry2)).thenReturn(eDto2);

        var foundEntries = passwordEntryService.findAll();

        Assertions.assertNotNull(foundEntries);
        Assertions.assertEquals(2, foundEntries.size());

        var idList = foundEntries.stream().map(PasswordEntryResponseDto::id).toList();
        Assertions.assertEquals(List.of(1L, 2L), idList);
    }

    @Test
    public void findAll_WhenEntriesDoesNotExists_ReturnListPasswordEntryResponseDto() {
        List<PasswordEntry> entries = List.of();
        when(passwordEntryRepository.findAll()).thenReturn(entries);

        var foundEntries = passwordEntryService.findAll();

        Assertions.assertNotNull(foundEntries);
        Assertions.assertTrue(foundEntries.isEmpty());

        verify(passwordEntryMapper, Mockito.never()).toPasswordEntryResponseDto(any(PasswordEntry.class));
    }

    @Test
    public void countAllEntryByUserId_ReturnNumberOfEntries() {
        Long numberOfEntries = 10L;
        when(passwordEntryRepository.countAllByUserId(userId)).thenReturn(numberOfEntries);

        var count = passwordEntryService.countAllEntryByUserId(userId);

        Assertions.assertEquals(numberOfEntries, count);
    }

    @Test
    public void deleteByUserId_ShouldDeleteTargetEntry() {
        passwordEntryService.deleteByUserId(userId);

        verify(passwordEntryRepository).deleteByUserId(userId);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteByServiceName_ShouldDeleteTargetEntry() {
        var serviceName = "service";

        passwordEntryService.deleteByServiceName(userId, serviceName);

        verify(passwordEntryRepository).deleteAllByUserIdAndServiceName(userId, serviceName);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteByCreatedAtBetween_ShouldDeleteTargetEntry() {
        var startDate = LocalDateTime.now().minusHours(1);
        var endDate = LocalDateTime.now().plusHours(1);

        passwordEntryService.deleteByCreatedAtBetween(userId, startDate, endDate);

        verify(passwordEntryRepository).deleteAllByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }

    @Test
    public void deleteById_ShouldDeleteTargetEntry() {
        passwordEntryService.deleteById(userId, passwordEntryId);

        verify(passwordEntryRepository).deleteById(passwordEntryId);
        verify(eventPublisher).publishEvent(any(AuditEventDto.class));
    }
}
