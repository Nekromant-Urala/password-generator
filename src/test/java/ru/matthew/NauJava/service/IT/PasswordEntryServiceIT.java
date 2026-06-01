package ru.matthew.NauJava.service.IT;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.audit.EventType;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec;
import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionService;
import ru.matthew.NauJava.domain.crypto.generation.RandomGeneratorService;
import ru.matthew.NauJava.domain.password.PasswordEntry;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.password.PasswordEntryServiceImpl;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryRequestDto;
import ru.matthew.NauJava.domain.password.dto.PasswordEntryUpdateDto;
import ru.matthew.NauJava.domain.password.exception.PasswordEntryNotFoundException;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static ru.matthew.NauJava.domain.user.Role.USER;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
public class PasswordEntryServiceIT {

    @Autowired
    private PasswordEntryServiceImpl passwordEntryService;

    @Autowired
    private PasswordEntryRepository passwordEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ApplicationEvents applicationEvents;

    @MockitoBean
    private EncryptionService encryptionService;

    @MockitoBean
    private RandomGeneratorService generatorService;

    private User savedUser;
    private Profile savedProfile;

    @BeforeEach
    public void setUpCommon() throws Exception {
        savedUser = createUser();
        savedUser = userRepository.save(savedUser);

        savedProfile = createProfile(savedUser, "Основной", true);
        savedProfile = profileRepository.save(savedProfile);
        savedUser.addProfile(savedProfile);

        Mockito.when(encryptionService.encrypt(any(), any(), any(), any()))
                .thenReturn(new byte[]{1, 2, 3, 4});
        Mockito.when(encryptionService.decrypt(any(), any(), any(), any()))
                .thenReturn(new byte[]{'s', 'e', 'c', 'r', 'e', 't'});
        Mockito.when(generatorService.generatePassword(any()))
                .thenReturn(new char[]{'g', 'e', 'n', 'e', 'r', 'a', 't', 'e', 'd'});
    }

    @Nested
    class CreatePasswordEntryTests {

        @Test
        public void createPasswordEntry_WhenWithExplicitPassword_ShouldSaveAndPublishEvent() {
            var dto = new PasswordEntryRequestDto(
                    "my_login", "raw_password".toCharArray(), "Google", "Desc", "Основной"
            );

            var response = passwordEntryService.createPasswordEntry(savedUser.getId(), dto);

            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.login()).isEqualTo("my_login");
            Assertions.assertThat(response.serviceName()).isEqualTo("Google");

            var savedEntry = passwordEntryRepository.findById(response.id()).orElseThrow();
            Assertions.assertThat(savedEntry.getServiceName()).isEqualTo("Google");

            long auditCount = applicationEvents.stream(AuditEventDto.class)
                    .filter(e -> e.eventType() == EventType.CREATE_ENTRY).count();
            Assertions.assertThat(auditCount).isEqualTo(1);
        }

        @Test
        public void createPasswordEntry_WhenPasswordIsEmpty_ShouldGeneratePassword() {
            var dto = new PasswordEntryRequestDto(
                    "generated_login", null, "GitHub", "Desc", "Основной"
            );

            var response = passwordEntryService.createPasswordEntry(savedUser.getId(), dto);

            Assertions.assertThat(response).isNotNull();
            Mockito.verify(generatorService, Mockito.times(1)).generatePassword(any());
        }

        @Test
        public void createPasswordEntry_WhenUserNotFound_ShouldThrowUserNotFoundException() {
            var dto = new PasswordEntryRequestDto(
                    "login", "pass".toCharArray(), "Service", "Desc", "Основной"
            );

            Assertions.assertThatThrownBy(() -> passwordEntryService.createPasswordEntry(999L, dto))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        public void createPasswordEntry_WhenProfileNotFoundAndNoFavorite_ShouldThrowProfileNotFoundException() {
            savedProfile.setFavorite(false);
            profileRepository.save(savedProfile);

            var dto = new PasswordEntryRequestDto(
                    "login", "pass".toCharArray(), "Service", "Desc", "НесуществующийПрофиль"
            );

            Assertions.assertThatThrownBy(() -> passwordEntryService.createPasswordEntry(savedUser.getId(), dto))
                    .isInstanceOf(ProfileNotFoundException.class);
        }
    }

    @Nested
    class FindPasswordEntryTests {
        private PasswordEntry savedEntry;

        @BeforeEach
        public void setUpEntries() {
            savedEntry = new PasswordEntry();
            savedEntry.setLogin("test_login");
            savedEntry.setPassword("encrypted_string");
            savedEntry.setServiceName("Yandex");
            savedEntry.setDescription("Test Desc");
            savedEntry.setUser(savedUser);
            savedEntry.setProfile(savedProfile);
            savedUser.addPasswordEntries(savedEntry);

            savedEntry = passwordEntryRepository.save(savedEntry);

            entityManager.flush();
            entityManager.clear();
        }

        @Test
        public void findById_WhenExists_ShouldReturnDto() {
            var result = passwordEntryService.findById(savedEntry.getId());

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.login()).isEqualTo("test_login");
            Assertions.assertThat(result.serviceName()).isEqualTo("Yandex");
        }

        @Test
        public void findById_WhenNotExists_ShouldThrowPasswordEntryNotFoundException() {
            Assertions.assertThatThrownBy(() -> passwordEntryService.findById(999L))
                    .isInstanceOf(PasswordEntryNotFoundException.class);
        }

        @Test
        public void findByServiceName_ShouldReturnPagedResults() {
            var page = passwordEntryService.findByServiceName(savedUser.getId(), "Yandex", PageRequest.of(0, 10));

            Assertions.assertThat(page).isNotNull();
            Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
            Assertions.assertThat(page.getContent().getFirst().login()).isEqualTo("test_login");
        }

        @Test
        public void findByCreatedAt_ShouldReturnPagedResults() {
            LocalDateTime targetDate = LocalDateTime.now().minusDays(2);

            entityManager.createQuery("UPDATE PasswordEntry p SET p.createdAt = :date WHERE p.id = :id")
                    .setParameter("date", targetDate)
                    .setParameter("id", savedEntry.getId())
                    .executeUpdate();

            var page = passwordEntryService.findByCreatedAt(savedUser.getId(), targetDate, PageRequest.of(0, 10));
            Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        }

        @Test
        public void findByUpdatedAt_ShouldReturnPagedResults() {
            LocalDateTime targetDate = LocalDateTime.now().minusDays(1);

            entityManager.createQuery("UPDATE PasswordEntry p SET p.updatedAt = :date WHERE p.id = :id")
                    .setParameter("date", targetDate)
                    .setParameter("id", savedEntry.getId())
                    .executeUpdate();

            var page = passwordEntryService.findByUpdatedAt(savedUser.getId(), targetDate, PageRequest.of(0, 10));
            Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        }

        @Test
        public void findByCreatedAtBetween_ShouldReturnPagedResults() {
            LocalDateTime start = LocalDateTime.now().minusDays(5);
            LocalDateTime end = LocalDateTime.now().minusDays(1);
            LocalDateTime targetDate = LocalDateTime.now().minusDays(3);

            entityManager.createQuery("UPDATE PasswordEntry p SET p.createdAt = :date WHERE p.id = :id")
                    .setParameter("date", targetDate)
                    .setParameter("id", savedEntry.getId())
                    .executeUpdate();

            var page = passwordEntryService.findByCreatedAtBetween(savedUser.getId(), start, end, PageRequest.of(0, 10));
            Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        }

        @Test
        public void findAllByPageForUser_ShouldReturnUserEntries() {
            var page = passwordEntryService.findAllByPageForUser(savedUser.getId(), PageRequest.of(0, 10));
            Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        }

        @Test
        public void findAll_ShouldReturnAllEntriesInSystem() {
            List<PasswordEntry> all = passwordEntryRepository.findAll();
            var dtoList = passwordEntryService.findAll();
            Assertions.assertThat(dtoList.size()).isEqualTo(all.size());
        }

        @Test
        public void countAllEntryByUserId_ShouldReturnCorrectCount() {
            long count = passwordEntryService.countAllEntryByUserId(savedUser.getId());
            Assertions.assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    class RevealAndUpdatePasswordTests {
        private PasswordEntry savedEntry;

        @BeforeEach
        public void setUp() {
            savedEntry = new PasswordEntry();
            savedEntry.setLogin("reveal_login");
            savedEntry.setPassword("encrypted_blob");
            savedEntry.setServiceName("CryptoService");
            savedEntry.setUser(savedUser);
            savedEntry.setProfile(savedProfile);
            savedEntry = passwordEntryRepository.save(savedEntry);

            entityManager.flush();
            entityManager.clear();
        }

        @Test
        public void revealPassword_WhenExists_ShouldReturnDecryptedPassword() {
            var response = passwordEntryService.revealPassword(savedEntry.getId());

            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(new String(response.pass())).isEqualTo("secret");
        }

        @Test
        public void updatePatchEntry_WhenWithNewPassword_ShouldUpdateAndPublishEvent() {
            var updateDto = new PasswordEntryUpdateDto(
                    "NewServiceName", "new_login", "new_pass".toCharArray(), "new desc"
            );

            var response = passwordEntryService.updatePatchEntry(savedEntry.getId(), updateDto);

            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.serviceName()).isEqualTo("NewServiceName");
            Assertions.assertThat(response.login()).isEqualTo("new_login");

            long auditCount = applicationEvents.stream(AuditEventDto.class)
                    .filter(e -> e.eventType() == EventType.UPDATE_ENTRY).count();
            Assertions.assertThat(auditCount).isEqualTo(1);
        }
    }

    @Nested
    class DeletePasswordEntryTests {
        private PasswordEntry savedEntry;

        @BeforeEach
        public void setUp() {
            savedEntry = new PasswordEntry();
            savedEntry.setLogin("delete_login");
            savedEntry.setPassword("blob");
            savedEntry.setServiceName("DeleteMe");
            savedEntry.setUser(savedUser);
            savedEntry.setProfile(savedProfile);
            savedEntry = passwordEntryRepository.save(savedEntry);

            entityManager.flush();
            entityManager.clear();
        }

        @Test
        public void deleteById_ShouldRemoveTargetEntry() {
            passwordEntryService.deleteById(savedUser.getId(), savedEntry.getId());

            Assertions.assertThat(passwordEntryRepository.findById(savedEntry.getId())).isEmpty();

            long auditCount = applicationEvents.stream(AuditEventDto.class)
                    .filter(e -> e.eventType() == EventType.DELETE_ENTRY).count();
            Assertions.assertThat(auditCount).isEqualTo(1);
        }

        @Test
        public void deleteByServiceName_ShouldRemoveMatchingEntries() {
            passwordEntryService.deleteByServiceName(savedUser.getId(), "DeleteMe");

            var page = passwordEntryService.findByServiceName(savedUser.getId(), "DeleteMe", PageRequest.of(0, 10));
            Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        }

        @Test
        public void deleteByUserId_ShouldRemoveAllUserEntries() {
            passwordEntryService.deleteByUserId(savedUser.getId());

            long count = passwordEntryService.countAllEntryByUserId(savedUser.getId());
            Assertions.assertThat(count).isEqualTo(0);
        }

        @Test
        public void deleteByCreatedAtBetween_ShouldRemoveEntriesInTimeFrame() {
            LocalDateTime start = LocalDateTime.now().minusDays(5);
            LocalDateTime end = LocalDateTime.now().minusDays(1);
            LocalDateTime targetDate = LocalDateTime.now().minusDays(3);

            entityManager.createQuery("UPDATE PasswordEntry p SET p.createdAt = :date WHERE p.id = :id")
                    .setParameter("date", targetDate)
                    .setParameter("id", savedEntry.getId())
                    .executeUpdate();

            passwordEntryService.deleteByCreatedAtBetween(savedUser.getId(), start, end);

            Assertions.assertThat(passwordEntryRepository.findById(savedEntry.getId())).isEmpty();
        }
    }

    private User createUser() {
        var user = new User();
        user.setUsername("pwd_user_" + System.currentTimeMillis());
        user.setEmail("pwd_test_" + System.currentTimeMillis() + "@mail.ru");
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword("user_hashed_password");
        user.setRole(USER);
        return user;
    }

    private Profile createProfile(User user, String name, boolean isFavorite) {
        var profile = new Profile.ProfileBuilder()
                .name(name)
                .passwordLength(16)
                .uppercase(true)
                .lowercase(true)
                .digits(true)
                .specialChars(true)
                .duplicateChars(true)
                .favorite(isFavorite)
                .customChars("")
                .kdfAlgorithm(Pbkdf2Spec.PBKDF_2)
                .cipher(CipherAlgorithmSpec.AES)
                .build();
        profile.setUser(user);
        return profile;
    }
}
