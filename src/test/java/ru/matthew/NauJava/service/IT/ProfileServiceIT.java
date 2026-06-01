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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;
import ru.matthew.NauJava.domain.audit.EventType;
import ru.matthew.NauJava.domain.audit.dto.AuditEventDto;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.profile.ProfileServiceImpl;
import ru.matthew.NauJava.domain.profile.dto.ProfileRequestDto;
import ru.matthew.NauJava.domain.profile.dto.ProfileResponseDto;
import ru.matthew.NauJava.domain.profile.exception.ProfileAlreadyExistsException;
import ru.matthew.NauJava.domain.profile.exception.ProfileNotFoundException;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.time.LocalDateTime;

import static ru.matthew.NauJava.domain.user.Role.USER;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
public class ProfileServiceIT {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationEvents applicationEvents;
    @Autowired
    private ProfileServiceImpl profileService;
    @Autowired
    private EntityManager entityManager;

    @Nested
    class CreateProfileTests {
        private Long userId;
        private User savedUser;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);
            userId = savedUser.getId();
        }

        @Test
        public void createProfile_WhenSuccessCreate_ReturnProfileResponseDto() {
            var requestDto = createRequestDto("name");

            var createdProfile = profileService.createProfile(userId, requestDto);

            Assertions.assertThat(createdProfile).isNotNull();
            Assertions.assertThat(createdProfile.name()).isEqualTo("name");
            Assertions.assertThat(createdProfile.kdfAlgorithm()).isEqualTo(Pbkdf2Spec.PBKDF_2.getName());
            Assertions.assertThat(createdProfile.cipher()).isEqualTo(CipherAlgorithmSpec.AES.getName());

            var savedProfile = profileRepository.findById(createdProfile.id()).orElseThrow();
            Assertions.assertThat(savedProfile.getName()).isEqualTo("name");

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.CREATE_PROFILE);
        }

        @Test
        public void createProfile_WhenNotFoundUser_ThrowUserNotFoundException() {
            var nonExistentId = 999L;
            var requestDto = createRequestDto("name");

            Assertions.assertThatThrownBy(() -> profileService.createProfile(nonExistentId, requestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь по заданному id: '%d' не был найден.".formatted(nonExistentId));

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(0);
        }

        @Test
        public void createProfile_WhenProfileNameAlreadyExists_ThrowProfileAlreadyExistsException() {
            var existentProfile = createProfile("name");
            existentProfile.setUser(savedUser);
            savedUser.addProfile(existentProfile);
            profileRepository.save(existentProfile);

            var requestDto = createRequestDto("name");

            Assertions.assertThatThrownBy(() -> profileService.createProfile(userId, requestDto))
                    .isInstanceOf(ProfileAlreadyExistsException.class)
                    .hasMessageContaining("Профиль-генерации с таким именем уже существует");

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(0);
        }

        @Test
        public void createDefaultProfile_WhenUserExists() {
            var nameProfile = "default";

            profileService.createDefaultProfile(userId);
            var foundProfile = profileRepository.findByUserIdAndName(userId, nameProfile).orElseThrow();

            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.getName()).isEqualTo(nameProfile);
            Assertions.assertThat(foundProfile.getUser().getId()).isEqualTo(userId);
            Assertions.assertThat(foundProfile.getKdfAlgorithm()).isEqualTo(Pbkdf2Spec.PBKDF_2);
            Assertions.assertThat(foundProfile.getCipher()).isEqualTo(CipherAlgorithmSpec.AES);
        }

        @Test
        public void createDefaultProfile_WhenUserDoesNotExists_ThrowUserNotFoundException() {
            var nameProfile = "default";
            var nonExistentId = 999L;

            Assertions.assertThatThrownBy(() -> profileService.createDefaultProfile(nonExistentId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("Пользователь по заданному id: '%d' не был найден.".formatted(nonExistentId));

            var foundProfile = profileRepository.findByUserIdAndName(userId, nameProfile);
            Assertions.assertThat(foundProfile).isEmpty();
        }
    }

    @Nested
    class FindProfileTests {
        private Long userId;
        private User savedUser;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);
            userId = savedUser.getId();
        }

        @Test
        public void findById_WhenProfileExists_ReturnProfileResponseDto() {
            var profile = createProfile("name");
            profile.setUser(savedUser);
            savedUser.addProfile(profile);
            profile = profileRepository.save(profile);

            entityManager.flush();
            entityManager.clear();

            var foundProfile = profileService.findById(profile.getId());

            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.id()).isEqualTo(profile.getId());
            Assertions.assertThat(foundProfile.name()).isEqualTo(profile.getName());
        }

        @Test
        public void findById_WhenProfileDoesNotExists_ThrowProfileNotFoundException() {
            var nonExistentId = 999L;

            Assertions.assertThatThrownBy(() -> profileService.findById(nonExistentId))
                    .isInstanceOf(ProfileNotFoundException.class)
                    .hasMessageContaining("Профайл генерации не был найден по id:%d".formatted(nonExistentId));
        }

        @Test
        public void findByName_WhenProfileExists_ReturnProfileResponseDto() {
            var profile = createProfile("name");
            profile.setUser(savedUser);
            savedUser.addProfile(profile);
            profile = profileRepository.save(profile);

            entityManager.flush();
            entityManager.clear();

            var foundProfile = profileService.findByName(userId, profile.getName());

            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.id()).isEqualTo(profile.getId());
            Assertions.assertThat(foundProfile.name()).isEqualTo(profile.getName());
        }

        @Test
        public void findByName_WhenProfileDoesNotExists_ThrowProfileNotFoundException() {
            var nonExistentName = "nonExistentName";

            Assertions.assertThatThrownBy(() -> profileService.findByName(userId, nonExistentName))
                    .isInstanceOf(ProfileNotFoundException.class)
                    .hasMessageContaining("Профайл генерации c именем:%s не был найден".formatted(nonExistentName));
        }

        @Test
        public void findAllByCreatedAtBetween_WhenProfileExists_ReturnPaginateProfileResponseDto() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime beforeWindow = now.minusDays(2);
            LocalDateTime afterWindow = now.plusDays(2);
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

            entityManager.flush();

            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", beforeWindow.minusDays(1))
                    .setParameter("name", "name1")
                    .executeUpdate();
            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", now.plusDays(1))
                    .setParameter("name", "name2")
                    .executeUpdate();
            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", now.minusHours(1))
                    .setParameter("name", "name3")
                    .executeUpdate();

            entityManager.flush();
            entityManager.clear();

            var foundProfilePage = profileService.findAllByCreatedAtBetween(userId, beforeWindow, afterWindow, pageable);

            Assertions.assertThat(foundProfilePage).isNotNull();
            Assertions.assertThat(foundProfilePage.getTotalElements()).isEqualTo(2);
            Assertions.assertThat(foundProfilePage.getTotalPages()).isEqualTo(1);
            Assertions.assertThat(foundProfilePage.getNumberOfElements()).isEqualTo(2);

            Assertions.assertThat(foundProfilePage.getContent()).extracting(ProfileResponseDto::name)
                    .containsExactly("name2", "name3");

        }

        @Test
        public void findAllByCreatedAtBetween_WhenProfileDoesNotExists_ReturnEmptyPaginate() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime beforeWindow = now.minusDays(2);
            LocalDateTime afterWindow = now.plusDays(2);
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

            var foundProfilePage = profileService.findAllByCreatedAtBetween(userId, beforeWindow, afterWindow, pageable);

            Assertions.assertThat(foundProfilePage).isNotNull();
            Assertions.assertThat(foundProfilePage.getContent()).hasSize(0);
            Assertions.assertThat(foundProfilePage.getTotalElements()).isEqualTo(0);
            Assertions.assertThat(foundProfilePage.getTotalPages()).isEqualTo(0);
            Assertions.assertThat(foundProfilePage.getNumberOfElements()).isEqualTo(0);
        }

        @Test
        public void findAllByCreateAt_WhenProfileExists_ReturnPaginateProfileResponseDto() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            entityManager.flush();

            LocalDateTime targetDate = LocalDateTime.now().minusDays(5);
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", targetDate)
                    .setParameter("name", "name1")
                    .executeUpdate();

            entityManager.flush();
            entityManager.clear();

            var foundProfilePage = profileService.findAllByCreateAt(userId, targetDate, pageable);

            Assertions.assertThat(foundProfilePage).isNotNull();
            Assertions.assertThat(foundProfilePage.getTotalElements()).isEqualTo(1);
            Assertions.assertThat(foundProfilePage.getTotalPages()).isEqualTo(1);
            Assertions.assertThat(foundProfilePage.getNumberOfElements()).isEqualTo(1);

            Assertions.assertThat(foundProfilePage.getContent()).extracting(ProfileResponseDto::name)
                    .containsExactly("name1");
        }

        @Test
        public void findAllByCreateAt_WhenProfileDoesNotExists_ReturnEmptyPaginate() {
            LocalDateTime targetDate = LocalDateTime.now().minusDays(5);
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

            var foundProfilePage = profileService.findAllByCreateAt(userId, targetDate, pageable);

            Assertions.assertThat(foundProfilePage).isNotNull();
            Assertions.assertThat(foundProfilePage.getContent()).hasSize(0);
            Assertions.assertThat(foundProfilePage.getTotalElements()).isEqualTo(0);
            Assertions.assertThat(foundProfilePage.getTotalPages()).isEqualTo(0);
            Assertions.assertThat(foundProfilePage.getNumberOfElements()).isEqualTo(0);
        }

        @Test
        public void findAllByUserId_WhenProfileExists_ReturnPaginateProfileResponseDto() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

            entityManager.flush();
            entityManager.clear();

            var foundProfilePage = profileService.findAllByUserId(userId, pageable);

            Assertions.assertThat(foundProfilePage).isNotNull();
            Assertions.assertThat(foundProfilePage.getTotalElements()).isEqualTo(3);
            Assertions.assertThat(foundProfilePage.getTotalPages()).isEqualTo(2);
            Assertions.assertThat(foundProfilePage.getNumberOfElements()).isEqualTo(2);

            Assertions.assertThat(foundProfilePage.getContent()).extracting(ProfileResponseDto::name)
                    .containsExactly("name1", "name2");
        }

        @Test
        public void findAllByUserId_WhenProfileDoesNotExists_ReturnEmptyPaginate() {
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

            var foundProfilePage = profileService.findAllByUserId(userId, pageable);

            Assertions.assertThat(foundProfilePage).isNotNull();
            Assertions.assertThat(foundProfilePage.getContent()).hasSize(0);
            Assertions.assertThat(foundProfilePage.getTotalElements()).isEqualTo(0);
            Assertions.assertThat(foundProfilePage.getTotalPages()).isEqualTo(0);
            Assertions.assertThat(foundProfilePage.getNumberOfElements()).isEqualTo(0);
        }

        @Test
        public void findAll_WhenProfileExists_ReturnProfileResponseDtoList() {
            var profile1 = createProfile("name1");
            var profile3 = createProfile("name2");
            var profile2 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            entityManager.flush();
            entityManager.clear();

            var foundProfile = profileService.findAll();

            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile).hasSize(3);
            Assertions.assertThat(foundProfile).extracting(ProfileResponseDto::name)
                    .contains("name1", "name2", "name3");
        }

        @Test
        public void findAll_WhenProfileDoesNotExists_ReturnEmptyList() {
            var foundProfile = profileService.findAll();

            Assertions.assertThat(foundProfile.isEmpty()).isTrue();
        }
    }

    @Nested
    class DeleteProfileTests {
        private Long userId;
        private User savedUser;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);
            userId = savedUser.getId();
        }

        @Test
        public void deleteAllByUserId_WhenUserExists_ShouldTagetUser() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            profileService.deleteAllByUserId(userId);

            var foundProfile = profileRepository.findAllByUserId(userId, PageRequest.of(0, 2));
            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.getTotalPages()).isEqualTo(0);
            Assertions.assertThat(foundProfile.getTotalElements()).isEqualTo(0);

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.DELETE_PROFILE);
        }

        @Test
        public void deleteAllByCreatedAt_WhenUserExists_ShouldTagetUser() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            entityManager.flush();

            LocalDateTime targetDate = LocalDateTime.now().minusDays(5);

            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", targetDate)
                    .setParameter("name", "name1")
                    .executeUpdate();

            entityManager.flush();
            entityManager.clear();

            profileService.deleteAllByCreatedAt(userId, targetDate);

            var foundProfile = profileRepository.findAllByUserId(userId, PageRequest.of(0, 2));
            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.getTotalPages()).isEqualTo(1);
            Assertions.assertThat(foundProfile.getTotalElements()).isEqualTo(2);
            Assertions.assertThat(foundProfile.getNumberOfElements()).isEqualTo(2);

            Assertions.assertThat(foundProfile.getContent()).extracting(Profile::getName)
                    .containsExactly("name2", "name3");

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.DELETE_PROFILE);
        }

        @Test
        public void deleteAllByCreatedAtBetween_WhenUserExists_ShouldTagetUser() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime beforeWindow = now.minusDays(2);
            LocalDateTime insideWindow = now;
            LocalDateTime afterWindow = now.plusDays(2);

            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", beforeWindow)
                    .setParameter("name", "name1")
                    .executeUpdate();
            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", insideWindow)
                    .setParameter("name", "name2")
                    .executeUpdate();
            entityManager.createQuery("UPDATE Profile p SET p.createAt = :date WHERE p.name = :name")
                    .setParameter("date", afterWindow)
                    .setParameter("name", "name3")
                    .executeUpdate();

            entityManager.flush();
            entityManager.clear();

            profileService.deleteAllByCreatedAtBetween(userId, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

            var foundProfile = profileRepository.findAllByUserId(userId, PageRequest.of(0, 2));
            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.getTotalPages()).isEqualTo(1);
            Assertions.assertThat(foundProfile.getTotalElements()).isEqualTo(2);
            Assertions.assertThat(foundProfile.getNumberOfElements()).isEqualTo(2);

            Assertions.assertThat(foundProfile.getContent()).extracting(Profile::getName)
                    .containsExactly("name1", "name3");

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.DELETE_PROFILE);
        }

        @Test
        public void deleteByName_WhenUserExists_ShouldTagetUser() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            profileService.deleteByName(userId, profile1.getName());

            var foundProfile = profileRepository.findAllByUserId(userId, PageRequest.of(0, 2));
            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.getTotalPages()).isEqualTo(1);
            Assertions.assertThat(foundProfile.getTotalElements()).isEqualTo(2);
            Assertions.assertThat(foundProfile.getNumberOfElements()).isEqualTo(2);

            Assertions.assertThat(foundProfile.getContent()).extracting(Profile::getName)
                    .containsExactly("name2", "name3");

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.DELETE_PROFILE);
        }

        @Test
        public void deleteById_WhenUserExists_ShouldTagetUser() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            entityManager.flush();
            entityManager.clear();

            profileService.deleteById(userId, profile1.getId());

            var foundProfile = profileRepository.findAllByUserId(userId, PageRequest.of(0, 2));
            Assertions.assertThat(foundProfile).isNotNull();
            Assertions.assertThat(foundProfile.getTotalPages()).isEqualTo(1);
            Assertions.assertThat(foundProfile.getTotalElements()).isEqualTo(2);
            Assertions.assertThat(foundProfile.getNumberOfElements()).isEqualTo(2);

            Assertions.assertThat(foundProfile.getContent()).extracting(Profile::getName)
                    .containsExactly("name2", "name3");

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.DELETE_PROFILE);
        }
    }

    @Nested
    class FavoriteAndUpdateSettingTests {
        private Long userId;
        private User savedUser;

        @BeforeEach
        public void setUp() {
            savedUser = createUser();
            savedUser = userRepository.save(savedUser);
            userId = savedUser.getId();
        }

        @Test
        public void setProfileAsFavorite_WhenProfileExists_ReturnProfileResponseDto() {
            var profile1 = createProfile("name1");
            profile1.setFavorite(true);
            var profile2 = createProfile("name2");
            profile2.setFavorite(false);

            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);

            profileRepository.save(profile1);
            profileRepository.save(profile2);

            entityManager.flush();
            entityManager.clear();

            profileService.setProfileAsFavorite(userId, profile2.getId());

            var updatedProfile1 = profileRepository.findById(profile1.getId()).orElseThrow();
            var updatedProfile2 = profileRepository.findById(profile2.getId()).orElseThrow();

            Assertions.assertThat(updatedProfile1.isFavorite()).isFalse();
            Assertions.assertThat(updatedProfile2.isFavorite()).isTrue();

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.UPDATE_PROFILE);
        }

        @Test
        public void setProfileAsFavorite_WhenProfileAlreadyFavorite_ShouldDoNothing() {
            var profile1 = createProfile("name1");
            profile1.setFavorite(true);

            profile1.setUser(savedUser);
            savedUser.addProfile(profile1);

            profileRepository.save(profile1);

            entityManager.flush();
            entityManager.clear();

            profileService.setProfileAsFavorite(userId, profile1.getId());

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(0);
        }

        @Test
        public void setProfileAsFavorite_WhenProfileNotFound_ThrowProfileNotFoundException() {
            var nonExistentId = 999L;

            Assertions.assertThatThrownBy(() -> profileService.setProfileAsFavorite(userId, nonExistentId))
                    .isInstanceOf(ProfileNotFoundException.class)
                    .hasMessageContaining("Профиль не найден или не принадлежит пользователю");
        }

        @Test
        public void updateSettings_WhenUserExists_ReturnProfileResponseDto() {
            var profile = createProfile("oldName");
            profile.setUser(savedUser);
            savedUser.addProfile(profile);
            profileRepository.save(profile);

            entityManager.flush();
            entityManager.clear();

            var updateDto = createRequestDto("newName");

            var updatedProfile = profileService.updateSettings(userId, profile.getId(), updateDto);

            Assertions.assertThat(updatedProfile).isNotNull();
            Assertions.assertThat(updatedProfile.name()).isEqualTo("newName");

            var savedProfile = profileRepository.findById(profile.getId()).orElseThrow();
            Assertions.assertThat(savedProfile.getName()).isEqualTo("newName");

            var auditEventCount = applicationEvents.stream(AuditEventDto.class).count();
            Assertions.assertThat(auditEventCount).isEqualTo(1);

            var event = applicationEvents.stream(AuditEventDto.class).findFirst().orElseThrow();
            Assertions.assertThat(event.eventType()).isEqualTo(EventType.UPDATE_PROFILE);
        }

        @Test
        public void updateSettings_WhenProfileNotFound_ThrowProfileNotFoundException() {
            var nonExistentId = 999L;
            var updateDto = createRequestDto("newName");

            Assertions.assertThatThrownBy(() -> profileService.updateSettings(userId, nonExistentId, updateDto))
                    .isInstanceOf(ProfileNotFoundException.class);
        }

        @Test
        public void countAllProfilesByUserId_ReturnNumberOfProfiles() {
            var profile1 = createProfile("name1");
            var profile2 = createProfile("name2");
            var profile3 = createProfile("name3");
            profile1.setUser(savedUser);
            profile2.setUser(savedUser);
            profile3.setUser(savedUser);
            savedUser.addProfile(profile1);
            savedUser.addProfile(profile2);
            savedUser.addProfile(profile3);

            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);

            entityManager.flush();
            entityManager.clear();

            var count = profileService.countAllProfilesByUserId(userId);

            Assertions.assertThat(count).isEqualTo(3);
        }
    }

    private User createUser() {
        var user = new User();
        user.setUsername("username");
        user.setEmail("test@mail.ru");
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword("hash_password");
        user.setRole(USER);
        return user;
    }

    private ProfileRequestDto createRequestDto(String name) {
        return new ProfileRequestDto(
                name, 12, true, true, true,
                true, true, true, "",
                Pbkdf2Spec.PBKDF_2.getName(), CipherAlgorithmSpec.AES.getName()
        );
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
}
