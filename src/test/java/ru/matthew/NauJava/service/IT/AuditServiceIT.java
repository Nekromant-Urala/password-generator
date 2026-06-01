package ru.matthew.NauJava.service.IT;


import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.audit.AuditRepository;
import ru.matthew.NauJava.domain.audit.AuditServiceImpl;
import ru.matthew.NauJava.domain.audit.EventType;
import ru.matthew.NauJava.domain.audit.dto.AuditCreateDto;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;
import ru.matthew.NauJava.domain.audit.exception.AuditEventNotFoundException;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.time.LocalDateTime;

import static ru.matthew.NauJava.domain.user.Role.USER;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class AuditServiceIT {

    @Autowired
    private AuditRepository auditRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEntryRepository passwordEntryRepository;
    @Autowired
    private AuditServiceImpl auditService;
    @Autowired
    private EntityManager entityManager;

    @Nested
    class CreateAuditTests {
        private User savedUser;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);
        }

        @Test
        public void createEvent_WhenSuccessCreate_ReturnAuditResponseDto() {
            var dto = new AuditCreateDto(
                    savedUser.getId(),
                    EventType.SIGN_UP_USER,
                    "Mozilla/5.0",
                    "Тестовое описание события"
            );

            var createdAudit = auditService.createEvent(dto);

            Assertions.assertThat(createdAudit).isNotNull();
            Assertions.assertThat(createdAudit.userId()).isEqualTo(savedUser.getId());
            Assertions.assertThat(createdAudit.type()).isEqualTo(EventType.SIGN_UP_USER);
            Assertions.assertThat(createdAudit.userAgent()).isEqualTo("Mozilla/5.0");
            Assertions.assertThat(createdAudit.description()).isEqualTo("Тестовое описание события");

            var savedAudit = auditRepository.findById(createdAudit.id()).orElseThrow();
            Assertions.assertThat(savedAudit.getEventType()).isEqualTo(EventType.SIGN_UP_USER);
        }

        @Test
        public void createEvent_WhenUserDoesNotExists_ThrowUserNotFoundException() {
            var nonExistentUserId = 999L;
            var dto = new AuditCreateDto(
                    nonExistentUserId,
                    EventType.SIGN_UP_USER,
                    "Mozilla/5.0",
                    "Тестовое описание"
            );

            Assertions.assertThatThrownBy(() -> auditService.createEvent(dto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь по заданному id: '%d' не был найден.".formatted(nonExistentUserId));
        }
    }

    @Nested
    class FindAuditTests {
        private User savedUser;
        private AuditResponseDto savedAudit;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);

            var dto = new AuditCreateDto(
                    savedUser.getId(),
                    EventType.SIGN_IN_USER,
                    "Test-Agent",
                    "Тестовое описание"
            );
            savedAudit = auditService.createEvent(dto);
        }

        @Test
        public void findById_WhenEventExists_ReturnAuditResponseDto() {
            var foundAudit = auditService.findById(savedAudit.id());

            Assertions.assertThat(foundAudit).isNotNull();
            Assertions.assertThat(foundAudit.id()).isEqualTo(savedAudit.id());
            Assertions.assertThat(foundAudit.type()).isEqualTo(EventType.SIGN_IN_USER);
        }

        @Test
        public void findById_WhenEventDoesNotExists_ThrowAuditEventNotFoundException() {
            var nonExistentId = 999L;

            Assertions.assertThatThrownBy(() -> auditService.findById(nonExistentId))
                    .isInstanceOf(AuditEventNotFoundException.class)
                    .hasMessageContaining("Не удалось найти события с таким id:%d".formatted(nonExistentId));
        }

        @Test
        public void findByEventType_WhenEventsExists_ReturnPage() {
            Pageable pageable = PageRequest.of(0, 10);
            var resultPage = auditService.findByEventType(EventType.SIGN_IN_USER, pageable);

            Assertions.assertThat(resultPage).isNotNull();
            Assertions.assertThat(resultPage.getTotalElements()).isGreaterThanOrEqualTo(1);
            Assertions.assertThat(resultPage.getContent()).extracting(AuditResponseDto::type)
                    .containsOnly(EventType.SIGN_IN_USER);
        }

        @Test
        public void findByUserAgent_WhenEventsExists_ReturnPage() {
            Pageable pageable = PageRequest.of(0, 10);
            var resultPage = auditService.findByUserAgent(savedUser.getId(), "Test-Agent", pageable);

            Assertions.assertThat(resultPage).isNotNull();
            Assertions.assertThat(resultPage.getTotalElements()).isGreaterThanOrEqualTo(1);
            Assertions.assertThat(resultPage.getContent()).extracting(AuditResponseDto::userAgent)
                    .containsOnly("Test-Agent");
        }

        @Test
        public void findByUserId_WhenEventsExists_ReturnPage() {
            Pageable pageable = PageRequest.of(0, 10);
            var resultPage = auditService.findByUserId(savedUser.getId(), pageable);

            Assertions.assertThat(resultPage).isNotNull();
            Assertions.assertThat(resultPage.getTotalElements()).isEqualTo(1);
            Assertions.assertThat(resultPage.getContent().getFirst().userId()).isEqualTo(savedUser.getId());
        }

        @Test
        public void findAll_WhenEventsExists_ReturnPage() {
            Pageable pageable = PageRequest.of(0, 10);
            var resultPage = auditService.findAll(pageable);

            Assertions.assertThat(resultPage).isNotNull();
            Assertions.assertThat(resultPage.getContent()).isNotEmpty();
        }

        @Test
        public void findByCreatedAt_WhenEventsExists_ReturnPage() {
            LocalDateTime targetDate = LocalDateTime.now().minusDays(5);

            entityManager.createQuery("UPDATE Audit a SET a.createdAt = :date WHERE a.id = :id")
                    .setParameter("date", targetDate)
                    .setParameter("id", savedAudit.id())
                    .executeUpdate();

            entityManager.flush();
            entityManager.clear();

            Pageable pageable = PageRequest.of(0, 10);
            var resultPage = auditService.findByCreatedAt(targetDate, pageable);

            Assertions.assertThat(resultPage).isNotNull();
            Assertions.assertThat(resultPage.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    class StatsAndCountsTests {
        private User savedUser;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);
        }

        @Test
        public void getAllStatsSystem_ShouldReturnCorrectStats() {
            auditService.createEvent(new AuditCreateDto(savedUser.getId(), EventType.CREATE_PROFILE, "Agent1", ""));
            auditService.createEvent(new AuditCreateDto(savedUser.getId(), EventType.UPDATE_PROFILE, "Agent2", ""));

            var profile = new Profile.ProfileBuilder()
                    .name("test-profile")
                    .passwordLength(12)
                    .cipher(CipherAlgorithmSpec.AES)
                    .kdfAlgorithm(Pbkdf2Spec.PBKDF_2)
                    .build();
            profile.setUser(savedUser);
            savedUser.addProfile(profile);
            profileRepository.save(profile);

            entityManager.flush();
            entityManager.clear();

            var stats = auditService.getAllStatsSystem();

            Assertions.assertThat(stats).isNotNull();
            Assertions.assertThat(stats.totalUser()).isGreaterThanOrEqualTo(1);
            Assertions.assertThat(stats.totalEvent()).isGreaterThanOrEqualTo(2);
            Assertions.assertThat(stats.totalUsersForLastDay()).isGreaterThanOrEqualTo(1);

            Assertions.assertThat(stats.totalEntries()).isGreaterThanOrEqualTo(0);
            Assertions.assertThat(stats.popularCipher()).isEqualTo(CipherAlgorithmSpec.AES.getName());
        }
    }

    @Nested
    class DeleteAuditTests {
        private User savedUser;
        private AuditResponseDto savedAudit;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);

            var dto = new AuditCreateDto(savedUser.getId(), EventType.DELETE_PROFILE, "Test-Agent", "desc");
            savedAudit = auditService.createEvent(dto);
        }

        @Test
        public void deleteById_WhenExists_ShouldDelete() {
            auditService.deleteById(savedAudit.id());

            Assertions.assertThatThrownBy(() -> auditService.findById(savedAudit.id()))
                    .isInstanceOf(AuditEventNotFoundException.class);
        }

        @Test
        public void deleteByUserId_WhenExists_ShouldDeleteAllUserEvents() {
            auditService.deleteByUserId(savedUser.getId());

            var page = auditService.findByUserId(savedUser.getId(), PageRequest.of(0, 10));
            Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        }

        @Test
        public void deleteByEventType_WhenExists_ShouldDelete() {
            auditService.deleteByEventType(EventType.DELETE_PROFILE);

            var page = auditService.findByEventType(EventType.DELETE_PROFILE, PageRequest.of(0, 10));
            Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        }

        @Test
        public void deleteByCreatedAt_WhenExists_ShouldDelete() {
            LocalDateTime targetDate = LocalDateTime.now().minusDays(10);

            entityManager.createQuery("UPDATE Audit a SET a.createdAt = :date WHERE a.id = :id")
                    .setParameter("date", targetDate)
                    .setParameter("id", savedAudit.id())
                    .executeUpdate();

            entityManager.flush();
            entityManager.clear();

            auditService.deleteByCreatedAt(targetDate);

            Assertions.assertThatThrownBy(() -> auditService.findById(savedAudit.id()))
                    .isInstanceOf(AuditEventNotFoundException.class);
        }
    }

    private User createUser() {
        var user = new User();
        user.setUsername("audit_test_user_" + System.currentTimeMillis());
        user.setEmail("audit_test_" + System.currentTimeMillis() + "@mail.ru");
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword("hash_password");
        user.setRole(USER);
        return user;
    }
}
