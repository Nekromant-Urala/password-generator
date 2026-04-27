package ru.matthew.NauJava.service.profile.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ru.matthew.NauJava.entity.AuditLog;
import ru.matthew.NauJava.entity.GeneratorProfile;
import ru.matthew.NauJava.entity.User;
import ru.matthew.NauJava.repository.exception.UserNotFoundException;
import ru.matthew.NauJava.repository.AuditLogRepository;
import ru.matthew.NauJava.repository.GeneratorProfileRepository;
import ru.matthew.NauJava.repository.UserRepository;
import ru.matthew.NauJava.service.profile.GeneratorProfileServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class GeneratorProfileServiceImplIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneratorProfileRepository generatorProfileRepository;

    @MockitoBean
    private AuditLogRepository auditLogRepository;

    @Autowired
    private GeneratorProfileServiceImpl profileService;

    private Long userId;
    private User testUser;
    private GeneratorProfile testProfile;

    @BeforeEach
    void setUp() {
        generatorProfileRepository.deleteAll();
        userRepository.deleteAll();

        testProfile = new GeneratorProfile();
        testProfile.setName("testProfile");
        testProfile.setPasswordLength(123);
        testProfile.setDigits(true);

        testUser = userRepository.save(new User());
        userId = testUser.getId();
    }

    @Test
    @DisplayName("успешного добавления профайла")
    void testSaveGeneratorProfile_Success() {

        profileService.saveGeneratorProfile(userId, testProfile);

        var savedProfile = generatorProfileRepository.findAll().getFirst();

        assertNotNull(savedProfile);
        assertNotNull(savedProfile.getUser());
        assertEquals("testProfile", savedProfile.getName());
        assertEquals(1, generatorProfileRepository.findAll().size());
        assertFalse(generatorProfileRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("отсутвие пользователя при поиске и соотевтсующее выбрасывание ошибки")
    void testSaveGeneratorProfile_UserNotFound_ThrowsException() {

        assertThrows(UserNotFoundException.class, () ->
                profileService.saveGeneratorProfile(-1L, testProfile)
        );
        assertEquals(0, generatorProfileRepository.findAll().size());
        assertTrue(generatorProfileRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("проверка отката транзакции при возникновении ошибки в AuditLog")
    void testSaveGeneratorProfile_WhenAuditLogFails_ShouldRollback() {

        // не удалось придумать вариант лучше замокать
        when(auditLogRepository.save(any(AuditLog.class))).thenThrow(new DataAccessException("DB Error") { });

        var exception = assertThrows(DataAccessException.class, () ->
                profileService.saveGeneratorProfile(userId, testProfile)
        );
        assertEquals("DB Error", exception.getMessage());

        assertEquals(0, generatorProfileRepository.findAll().size());
        assertTrue(generatorProfileRepository.findAll().isEmpty());
    }
}
