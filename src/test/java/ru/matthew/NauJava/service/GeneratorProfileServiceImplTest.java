package ru.matthew.NauJava.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.matthew.NauJava.entity.GeneratorProfile;
import ru.matthew.NauJava.entity.User;
import ru.matthew.NauJava.exception.UserNotFoundException;
import ru.matthew.NauJava.repository.GeneratorProfileRepository;
import ru.matthew.NauJava.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GeneratorProfileServiceIntegrationTest {

    @Autowired
    private GeneratorProfileService generatorProfileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneratorProfileRepository generatorProfileRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        generatorProfileRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hash123");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);
    }

    @RepeatedTest(5)
    void testCreateProfileForUserSuccess() {
        GeneratorProfile profileData = new GeneratorProfile();
        profileData.setName("Test Profile");
        profileData.setPasswordLength(12);
        profileData.setLowercase(true);

        GeneratorProfile createdProfile = generatorProfileService.createProfileForUser(
                testUser.getId(), profileData);

        assertNotNull(createdProfile.getId());
        assertEquals("Test Profile", createdProfile.getName());
    }

    @RepeatedTest(5)
    void testCreateProfileForUserNotFoundUser() {
        GeneratorProfile profileData = new GeneratorProfile();
        profileData.setName("Test Profile");
        profileData.setPasswordLength(12);
        profileData.setLowercase(true);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> generatorProfileService.createProfileForUser(1000L, profileData)
        );

        assertEquals("Пользователя не удалось найти!", exception.getMessage());
        assertEquals(0, generatorProfileRepository.count());
    }
}