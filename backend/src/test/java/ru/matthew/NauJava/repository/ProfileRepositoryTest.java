package ru.matthew.NauJava.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.user.User;

import java.time.LocalDateTime;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProfileRepositoryTest {

    private TestEntityManager entityManager;
    private ProfileRepository profileRepository;
    private User user;

    @Autowired
    public ProfileRepositoryTest(TestEntityManager entityManager, ProfileRepository profileRepository) {
        this.entityManager = entityManager;
        this.profileRepository = profileRepository;
    }

    private User createUser(String username, String email) {
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return user;
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
                .iterations(10000)
                .build();
    }

    @BeforeEach
    public void setUp() {
        entityManager.clear();

        user = createUser("username", "email@test.ru");
        entityManager.persistAndFlush(user);
    }

    @Test
    public void findByUserIdAndName_WhenProfileExists_ReturnProfile() {
        var profile = createProfile("name");
        user.addProfile(profile);
        profile.setUser(user);

        entityManager.persist(profile);
        entityManager.flush();
        entityManager.clear();

        var foundProfile = profileRepository.findByUserIdAndName(user.getId(), profile.getName());

        Assertions.assertThat(foundProfile).isPresent();
        Assertions.assertThat(foundProfile.get().getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(foundProfile.get().getName()).isEqualTo("name");
    }

    @Test
    public void findByUserIdAndName_WhenProfileDoesNotExists_ReturnOptionalEmpty() {
        var foundProfile = profileRepository.findByUserIdAndName(user.getId(), "nonExistsProfile");

        Assertions.assertThat(foundProfile).isEmpty();
    }

    @Test
    public void findByUserIdAndName_WhenUserDoesNotExists_ReturnOptionalEmpty() {
        var foundProfile = profileRepository.findByUserIdAndName(10000L, "nonExistsProfile");

        Assertions.assertThat(foundProfile).isEmpty();
    }

    @Test
    public void findAllByUserIdAndCreateAtBetween_WhenProfileExists_ReturnPaginatedProfiles() {
        var profile = createProfile("name");
        user.addProfile(profile);
        profile.setUser(user);

        entityManager.persistAndFlush(profile);
        entityManager.getEntityManager()
                .createQuery("UPDATE Profile p SET p.createAt = :pastTime WHERE p.id = :id")
                .setParameter("pastTime", LocalDateTime.now().minusHours(3))
                .setParameter("id", profile.getId())
                .executeUpdate();

        var anotherProfile1 = createProfile("anotherProfile1");
        var anotherProfile2 = createProfile("anotherProfile2");
        var anotherProfile3 = createProfile("anotherProfile3");

        user.addProfile(anotherProfile1);
        user.addProfile(anotherProfile2);
        user.addProfile(anotherProfile3);
        anotherProfile1.setUser(user);
        anotherProfile2.setUser(user);
        anotherProfile3.setUser(user);

        entityManager.flush();
        entityManager.clear();

        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime to = LocalDateTime.now().plusHours(1);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        Page<Profile> profiles = profileRepository.findAllByUserIdAndCreateAtBetween(user.getId(), from, to, pageable);

        Assertions.assertThat(profiles).isNotNull();

        Assertions.assertThat(profiles.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(profiles.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(profiles.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(profiles.hasNext()).isTrue();
        Assertions.assertThat(profiles.hasPrevious()).isFalse();

        Assertions.assertThat(profiles.getContent()).extracting(Profile::getName).containsExactly("anotherProfile1", "anotherProfile2");
    }

    @Test
    public void findAllByUserIdAndCreateAt_WhenProfileExists_ReturnPaginatedProfiles() {
        var profile1 = createProfile("name1");
        var profile2 = createProfile("name2");
        var profile3 = createProfile("name3");

        user.addProfile(profile1);
        user.addProfile(profile2);
        user.addProfile(profile3);
        profile1.setUser(user);
        profile2.setUser(user);
        profile3.setUser(user);

        entityManager.persist(profile1);
        entityManager.persist(profile2);
        entityManager.persist(profile3);
        var exactDbTime = profile1.getCreateAt();
        entityManager.flush();
        entityManager.clear();
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        Page<Profile> profiles = profileRepository.findAllByUserIdAndCreateAt(user.getId(), exactDbTime, pageable);

        Assertions.assertThat(profiles).isNotNull();

        Assertions.assertThat(profiles.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(profiles.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(profiles.getNumberOfElements()).isEqualTo(1);

        Assertions.assertThat(profiles.hasNext()).isFalse();
        Assertions.assertThat(profiles.hasPrevious()).isFalse();
    }

    @Test
    public void findAllByUserId_WhenProfileExists_ReturnPaginatedProfiles() {
        var profile1 = createProfile("name1");
        var profile2 = createProfile("name2");
        var profile3 = createProfile("name3");
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        user.addProfile(profile1);
        user.addProfile(profile2);
        user.addProfile(profile3);

        profile1.setUser(user);
        profile2.setUser(user);
        profile3.setUser(user);

        entityManager.persist(profile1);
        entityManager.persist(profile2);
        entityManager.persist(profile3);
        entityManager.flush();
        entityManager.clear();

        Page<Profile> profiles = profileRepository.findAllByUserId(user.getId(), pageable);

        Assertions.assertThat(profiles).isNotNull();

        Assertions.assertThat(profiles.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(profiles.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(profiles.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(profiles.hasNext()).isTrue();
        Assertions.assertThat(profiles.hasPrevious()).isFalse();

        Assertions.assertThat(profiles.getContent()).extracting(Profile::getName).containsExactly("name1", "name2");
    }

    @Test
    public void countAllByUserId_WhenProfilesExists_ReturnCountProfiles() {
        var profile1 = createProfile("name");
        var profile2 = createProfile("name2");
        var profile3 = createProfile("name3");

        profile1.setUser(user);
        profile2.setUser(user);
        profile3.setUser(user);

        user.addProfile(profile1);
        user.addProfile(profile2);
        user.addProfile(profile3);

        entityManager.persist(profile1);
        entityManager.persist(profile2);
        entityManager.persist(profile3);
        entityManager.flush();
        entityManager.clear();

        long countProfiles = profileRepository.countAllByUserId(user.getId());

        Assertions.assertThat(countProfiles).isEqualTo(3);
    }

    @Test
    public void countAllByUserId_WhenProfilesDoesNotExists_ReturnCountProfiles() {
        long countProfiles = profileRepository.countAllByUserId(user.getId());

        Assertions.assertThat(countProfiles).isEqualTo(0);
    }

    @Test
    public void deleteAllByUserId_WhenProfilesExists_ShouldDeleteOnlyTargetUserProfiles() {
        var profile1 = createProfile("name");
        var profile2 = createProfile("name2");

        profile1.setUser(user);
        profile2.setUser(user);
        user.addProfile(profile1);
        user.addProfile(profile2);

        entityManager.persist(profile1);
        entityManager.persist(profile2);

        var anotherUser = createUser("otherUser", "other.user@us.ru");
        var profile3 = createProfile("name3");

        profile3.setUser(anotherUser);
        anotherUser.addProfile(profile3);

        entityManager.persist(anotherUser);
        entityManager.persist(profile3);
        entityManager.flush();
        entityManager.clear();

        profileRepository.deleteAllByUserId(user.getId());

        long countTargetUser = profileRepository.countAllByUserId(user.getId());
        Assertions.assertThat(countTargetUser).isEqualTo(0);

        long countOtherUser = profileRepository.countAllByUserId(anotherUser.getId());
        Assertions.assertThat(countOtherUser).isEqualTo(1);
    }

    @Test
    public void deleteAllByUserIdAndCreateAt_WhenProfileExists_ShouldDeleteProfiles() {
        var profile = createProfile("name");
        user.addProfile(profile);
        profile.setUser(user);

        entityManager.persistAndFlush(profile);
        var exactDbTime = profile.getCreateAt();
        entityManager.clear();

        profileRepository.deleteAllByUserIdAndCreateAt(user.getId(), exactDbTime);

        var foundProfile = profileRepository.findById(profile.getId());
        Assertions.assertThat(foundProfile).isEmpty();
    }

    @Test
    public void deleteAllByUserIdAndCreateAtBetween_ShouldDeleteOnlyProfilesInsideRange() {
        var profile = createProfile("name");
        user.addProfile(profile);
        profile.setUser(user);

        entityManager.persistAndFlush(profile);
        entityManager.getEntityManager()
                .createQuery("UPDATE Profile p SET p.createAt = :pastTime WHERE p.id = :id")
                .setParameter("pastTime", LocalDateTime.now().minusHours(3))
                .setParameter("id", profile.getId())
                .executeUpdate();

        var anotherProfile = createProfile("anotherProfile");
        user.addProfile(anotherProfile);
        anotherProfile.setUser(user);
        entityManager.persistAndFlush(anotherProfile);
        entityManager.clear();

        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime to = LocalDateTime.now().plusHours(1);

        profileRepository.deleteAllByUserIdAndCreateAtBetween(user.getId(), from, to);

        var foundProfile = profileRepository.findById(profile.getId());
        Assertions.assertThat(foundProfile).isPresent();
        Assertions.assertThat(foundProfile.get().getName()).isEqualTo("name");

        var deletedProfile = profileRepository.findById(anotherProfile.getId());
        Assertions.assertThat(deletedProfile).isEmpty();
    }

    @Test
    public void deleteByUserIdAndName_ShouldDeleteProfile() {
        var profile = createProfile("name");
        var anotherProfile = createProfile("anotherName");

        user.addProfile(profile);
        user.addProfile(anotherProfile);
        profile.setUser(user);
        anotherProfile.setUser(user);

        entityManager.persistAndFlush(profile);
        entityManager.clear();

        profileRepository.deleteByUserIdAndName(user.getId(), profile.getName());

        var foundProfile = profileRepository.findById(anotherProfile.getId());
        Assertions.assertThat(foundProfile).isPresent();
        Assertions.assertThat(foundProfile.get().getName()).isEqualTo(anotherProfile.getName());

        var deletedProfile = profileRepository.findById(profile.getId());
        Assertions.assertThat(deletedProfile).isEmpty();
    }
}
