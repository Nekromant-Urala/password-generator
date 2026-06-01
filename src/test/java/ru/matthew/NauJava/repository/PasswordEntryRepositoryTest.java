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
import ru.matthew.NauJava.domain.password.PasswordEntry;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.profile.Profile;
import ru.matthew.NauJava.domain.user.Role;
import ru.matthew.NauJava.domain.user.User;

import java.time.LocalDateTime;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PasswordEntryRepositoryTest {

    private PasswordEntryRepository passwordEntryRepository;
    private TestEntityManager entityManager;
    private User user;
    private Long profileId;
    private Profile profile;

    @Autowired
    public PasswordEntryRepositoryTest(PasswordEntryRepository passwordEntryRepository, TestEntityManager entityManager) {
        this.passwordEntryRepository = passwordEntryRepository;
        this.entityManager = entityManager;
    }

    private User createUser(String username, String email) {
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hash_password");
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);
        return user;
    }

    private PasswordEntry createPasswordEntry(String serviceName, String login) {
        var entry = new PasswordEntry();

        entry.setServiceName(serviceName);
        entry.setLogin(login);
        entry.setPassword("hash_password");
        entry.setDescription("desc");
        return entry;
    }

    private Profile createProfile() {
        var profile = new Profile.ProfileBuilder()
                .name("name")
                .passwordLength(12)
                .cipher(CipherAlgorithmSpec.AES)
                .kdfAlgorithm(Pbkdf2Spec.PBKDF_2)
                .build();
        profile.setId(profileId);
        profile.setCreateAt(LocalDateTime.now());
        return profile;
    }

    @BeforeEach
    public void setUp() {
        entityManager.clear();

        profile = createProfile();
        user = createUser("username", "test@domain.ru");
        entityManager.persist(user);
        profile.setUser(user);
        entityManager.persist(profile);
        entityManager.flush();
    }

    @Test
    public void findAllByUserIdAndServiceName_WhenEntryExists_ReturnPaginatedEntries() {
        var entry1 = createPasswordEntry("service1", "login1");
        var entry2 = createPasswordEntry("service1", "login2");
        var entry3 = createPasswordEntry("service1", "login3");
        var entry4 = createPasswordEntry("service2", "login4");
        var pageable = PageRequest.of(0, 2, Sort.by("login").ascending());

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        user.addPasswordEntries(entry3);
        user.addPasswordEntries(entry4);
        entry1.setUser(user);
        entry2.setUser(user);
        entry3.setUser(user);
        entry4.setUser(user);
        entry1.setProfile(profile);
        entry2.setProfile(profile);
        entry3.setProfile(profile);
        entry4.setProfile(profile);

        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);
        entityManager.flush();
        entityManager.clear();

        Page<PasswordEntry> entries = passwordEntryRepository.findAllByUserIdAndServiceName(user.getId(), entry1.getServiceName(), pageable);

        Assertions.assertThat(entries).isNotNull();

        Assertions.assertThat(entries.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(entries.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(entries.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(entries.hasNext()).isTrue();
        Assertions.assertThat(entries.hasPrevious()).isFalse();

        Assertions.assertThat(entries.getContent()).extracting(PasswordEntry::getLogin).containsExactly("login1", "login2");
    }

    @Test
    public void findAllByUserIdAndCreatedAtBetween_WhenEntryExists_ReturnPaginatedEntries() {
        var entry = createPasswordEntry("serviceName", "login");
        entry.setUser(user);
        entry.setProfile(profile);
        user.addPasswordEntries(entry);

        entityManager.persistAndFlush(entry);
        entityManager.getEntityManager()
                .createQuery("UPDATE PasswordEntry e SET e.createdAt = :pastTime WHERE e.id = :id")
                .setParameter("pastTime", LocalDateTime.now().minusHours(3))
                .setParameter("id", entry.getId())
                .executeUpdate();

        var aEntry1 = createPasswordEntry("serviceName1", "login1");
        var aEntry2 = createPasswordEntry("serviceName2", "login2");
        var aEntry3 = createPasswordEntry("serviceName3", "login3");

        aEntry1.setUser(user);
        aEntry2.setUser(user);
        aEntry3.setUser(user);
        aEntry1.setProfile(profile);
        aEntry2.setProfile(profile);
        aEntry3.setProfile(profile);
        user.addPasswordEntries(aEntry1);
        user.addPasswordEntries(aEntry2);
        user.addPasswordEntries(aEntry3);

        entityManager.persist(aEntry1);
        entityManager.persist(aEntry2);
        entityManager.persist(aEntry3);
        entityManager.flush();
        entityManager.clear();

        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime to = LocalDateTime.now().plusHours(1);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("login").ascending());

        Page<PasswordEntry> entries = passwordEntryRepository.findAllByUserIdAndCreatedAtBetween(user.getId(), from, to, pageable);

        Assertions.assertThat(entries).isNotNull();

        Assertions.assertThat(entries.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(entries.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(entries.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(entries.hasNext()).isTrue();
        Assertions.assertThat(entries.hasPrevious()).isFalse();

        Assertions.assertThat(entries.getContent()).extracting(PasswordEntry::getLogin).containsExactly("login1", "login2");
    }

    @Test
    public void findAllByUserIdAndCreatedAt_WhenEntryExists_ReturnPaginatedEntries() {
        var entry1 = createPasswordEntry("serviceName1", "login1");
        var entry2 = createPasswordEntry("serviceName2", "login2");
        var pageable = PageRequest.of(0, 2, Sort.by("serviceName").ascending());

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        entry1.setUser(user);
        entry2.setUser(user);
        entry1.setProfile(profile);
        entry2.setProfile(profile);

        entityManager.persist(entry1);
        entityManager.persist(entry2);
        var exactDbTime = entry1.getCreatedAt();
        entityManager.flush();
        entityManager.clear();

        var foundEntry = passwordEntryRepository.findByUserIdAndCreatedAt(user.getId(), exactDbTime, pageable);

        Assertions.assertThat(foundEntry).isNotNull();

        Assertions.assertThat(foundEntry.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(foundEntry.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(foundEntry.getNumberOfElements()).isEqualTo(1);

        Assertions.assertThat(foundEntry.hasNext()).isFalse();
        Assertions.assertThat(foundEntry.hasPrevious()).isFalse();
    }

    @Test
    public void findAllByUserIdAndUpdatedAt_WhenEntryExists_ReturnPaginatedEntries() {
        var entry1 = createPasswordEntry("serviceName1", "login1");
        var entry2 = createPasswordEntry("serviceName2", "login2");
        var pageable = PageRequest.of(0, 2, Sort.by("serviceName").ascending());

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        entry1.setUser(user);
        entry2.setUser(user);
        entry1.setProfile(profile);
        entry2.setProfile(profile);

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);

        entry2.setPassword("new_hash_password");
        entityManager.flush();
        var exactDbTime = entry2.getUpdatedAt();
        entityManager.clear();

        var foundEntry = passwordEntryRepository.findByUserIdAndUpdatedAt(user.getId(), exactDbTime, pageable);

        Assertions.assertThat(foundEntry).isNotNull();

        Assertions.assertThat(foundEntry.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(foundEntry.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(foundEntry.getNumberOfElements()).isEqualTo(1);

        Assertions.assertThat(foundEntry.hasNext()).isFalse();
        Assertions.assertThat(foundEntry.hasPrevious()).isFalse();

        Assertions.assertThat(foundEntry.getContent()).extracting(PasswordEntry::getServiceName).containsExactly("serviceName2");
    }

    @Test
    public void findAllByUserId_WhenEntryExists_ReturnPaginatedEntries() {
        var entry1 = createPasswordEntry("service1", "login1");
        var entry2 = createPasswordEntry("service2", "login1");
        var entry3 = createPasswordEntry("service3", "login1");
        var entry4 = createPasswordEntry("service4", "login1");

        var oUser = createUser("oUser", "o.email@test.ru");
        var pageable = PageRequest.of(0, 2, Sort.by("serviceName").ascending());

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        user.addPasswordEntries(entry3);
        oUser.addPasswordEntries(entry4);

        entry1.setUser(user);
        entry2.setUser(user);
        entry3.setUser(user);
        entry4.setUser(oUser);
        entry1.setProfile(profile);
        entry2.setProfile(profile);
        entry3.setProfile(profile);
        entry4.setProfile(profile);

        entityManager.persist(oUser);
        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);

        entityManager.flush();
        entityManager.clear();

        var foundEntries = passwordEntryRepository.findAllByUserId(user.getId(), pageable);

        Assertions.assertThat(foundEntries).isNotNull();

        Assertions.assertThat(foundEntries.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(foundEntries.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(foundEntries.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(foundEntries.hasNext()).isTrue();
        Assertions.assertThat(foundEntries.hasPrevious()).isFalse();

        Assertions.assertThat(foundEntries.getContent()).extracting(PasswordEntry::getServiceName).containsExactly("service1", "service2");
    }

    @Test
    public void findAllByUserId_WhenEntryExists_ReturnListEntries() {
        var entry1 = createPasswordEntry("service1", "login1");
        var entry2 = createPasswordEntry("service2", "login1");
        var entry3 = createPasswordEntry("service3", "login1");
        var entry4 = createPasswordEntry("service4", "login1");

        var oUser = createUser("oUser", "o.email@test.ru");

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        user.addPasswordEntries(entry3);
        oUser.addPasswordEntries(entry4);

        entry1.setUser(user);
        entry2.setUser(user);
        entry3.setUser(user);
        entry4.setUser(oUser);
        entry1.setProfile(profile);
        entry2.setProfile(profile);
        entry3.setProfile(profile);
        entry4.setProfile(profile);

        entityManager.persist(oUser);
        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);

        entityManager.flush();
        entityManager.clear();

        var foundEntries = passwordEntryRepository.findAllByUserId(user.getId());

        Assertions.assertThat(foundEntries).isNotNull();
        Assertions.assertThat(foundEntries.size()).isEqualTo(3);
        Assertions.assertThat(foundEntries).extracting(PasswordEntry::getServiceName).containsExactly("service1", "service2", "service3");
    }

    @Test
    public void findAllByUsername_WhenEntryExists_ReturnListEntries() {
        var entry1 = createPasswordEntry("service1", "login1");
        var entry2 = createPasswordEntry("service2", "login1");
        var entry3 = createPasswordEntry("service3", "login1");
        var entry4 = createPasswordEntry("service4", "login1");

        var oUser = createUser("oUser", "o.email@test.ru");

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        user.addPasswordEntries(entry3);
        oUser.addPasswordEntries(entry4);

        entry1.setUser(user);
        entry2.setUser(user);
        entry3.setUser(user);
        entry4.setUser(oUser);
        entry1.setProfile(profile);
        entry2.setProfile(profile);
        entry3.setProfile(profile);
        entry4.setProfile(profile);

        entityManager.persist(oUser);
        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);


        entityManager.flush();
        entityManager.clear();

        var foundEntries = passwordEntryRepository.findAllByUsername(oUser.getUsername());

        Assertions.assertThat(foundEntries).isNotNull();
        Assertions.assertThat(foundEntries.size()).isEqualTo(1);
        Assertions.assertThat(foundEntries).extracting(PasswordEntry::getServiceName).containsExactly("service4");
    }

    @Test
    public void countAllByUserId_WhenEntryExists_ReturnCountEntries() {
        var entry1 = createPasswordEntry("service1", "login1");
        var entry2 = createPasswordEntry("service2", "login1");
        var entry3 = createPasswordEntry("service3", "login1");
        var entry4 = createPasswordEntry("service4", "login1");

        var oUser = createUser("oUser", "o.email@test.ru");

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        user.addPasswordEntries(entry3);
        oUser.addPasswordEntries(entry4);

        entry1.setUser(user);
        entry2.setUser(user);
        entry3.setUser(user);
        entry4.setUser(oUser);
        entry1.setProfile(profile);
        entry2.setProfile(profile);
        entry3.setProfile(profile);
        entry4.setProfile(profile);

        entityManager.persist(oUser);
        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);

        entityManager.flush();
        entityManager.clear();

        var countEntries = passwordEntryRepository.countAllByUserId(user.getId());

        Assertions.assertThat(countEntries).isEqualTo(3);
    }

    @Test
    public void deleteAllByUserIdAndCreatedAtBetween_ShouldDeleteOnlyEntriesInsideRange() {
        var entry = createPasswordEntry("serviceName", "login");
        entry.setUser(user);
        entry.setProfile(profile);
        user.addPasswordEntries(entry);

        entityManager.persistAndFlush(entry);
        entityManager.getEntityManager()
                .createQuery("UPDATE PasswordEntry e SET e.createdAt = :pastTime WHERE e.id = :id")
                .setParameter("pastTime", LocalDateTime.now().minusHours(3))
                .setParameter("id", entry.getId())
                .executeUpdate();

        var oEntry1 = createPasswordEntry("serviceName1", "login1");
        var oEntry2 = createPasswordEntry("serviceName2", "login2");
        var oEntry3 = createPasswordEntry("serviceName3", "login3");

        oEntry1.setUser(user);
        oEntry2.setUser(user);
        oEntry3.setUser(user);
        oEntry1.setProfile(profile);
        oEntry2.setProfile(profile);
        oEntry3.setProfile(profile);
        user.addPasswordEntries(oEntry1);
        user.addPasswordEntries(oEntry2);
        user.addPasswordEntries(oEntry3);

        entityManager.persist(oEntry1);
        entityManager.persist(oEntry2);
        entityManager.persist(oEntry3);
        entityManager.flush();
        entityManager.clear();

        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime to = LocalDateTime.now().plusHours(1);

        passwordEntryRepository.deleteAllByUserIdAndCreatedAtBetween(user.getId(), from, to);
        var foundEntries = passwordEntryRepository.findAll();

        Assertions.assertThat(foundEntries).isNotNull();
        Assertions.assertThat(foundEntries.size()).isEqualTo(1);
        Assertions.assertThat(foundEntries).extracting(PasswordEntry::getServiceName).containsExactly("serviceName");
    }

    @Test
    public void deleteByUserId_ShouldDeleteOnlyTargetUserEntries() {
        var entry1 = createPasswordEntry("service1", "login1");
        var entry2 = createPasswordEntry("service2", "login1");
        var entry3 = createPasswordEntry("service3", "login1");
        var entry4 = createPasswordEntry("service2", "login1");

        var oUser = createUser("oUser", "o.email@test.ru");

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        user.addPasswordEntries(entry3);
        oUser.addPasswordEntries(entry4);

        entry1.setUser(user);
        entry2.setUser(user);
        entry3.setUser(user);
        entry4.setUser(oUser);
        entry1.setProfile(profile);
        entry2.setProfile(profile);
        entry3.setProfile(profile);
        entry4.setProfile(profile);

        entityManager.persist(oUser);
        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);

        entityManager.flush();
        entityManager.clear();

        passwordEntryRepository.deleteByUserId(user.getId());
        var foundEntries = passwordEntryRepository.findAll();

        Assertions.assertThat(foundEntries).isNotNull();
        Assertions.assertThat(foundEntries.size()).isEqualTo(1);
        Assertions.assertThat(foundEntries).extracting(PasswordEntry::getServiceName).containsExactly("service2");
    }

    @Test
    public void deleteAllByUserIdAndServiceName_ShouldDeleteOnlyTargetServiceNameEntries() {
        var entry1 = createPasswordEntry("service1", "login1");
        var entry2 = createPasswordEntry("service2", "login1");
        var entry3 = createPasswordEntry("service3", "login1");
        var entry4 = createPasswordEntry("service2", "login1");

        user.addPasswordEntries(entry1);
        user.addPasswordEntries(entry2);
        user.addPasswordEntries(entry3);
        user.addPasswordEntries(entry4);

        entry1.setUser(user);
        entry2.setUser(user);
        entry3.setUser(user);
        entry4.setUser(user);
        entry1.setProfile(profile);
        entry2.setProfile(profile);
        entry3.setProfile(profile);
        entry4.setProfile(profile);

        entityManager.persist(entry1);
        entityManager.persist(entry2);
        entityManager.persist(entry3);
        entityManager.persist(entry4);

        entityManager.flush();
        entityManager.clear();

        passwordEntryRepository.deleteAllByUserIdAndServiceName(user.getId(), entry2.getServiceName());
        var foundEntries = passwordEntryRepository.findAll();

        Assertions.assertThat(foundEntries).isNotNull();
        Assertions.assertThat(foundEntries.size()).isEqualTo(2);
        Assertions.assertThat(foundEntries).extracting(PasswordEntry::getServiceName).containsExactly("service1", "service3");
    }
}
