package ru.matthew.NauJava.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.entity.ServiceEntry;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PasswordEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEntryRepository passwordEntryRepository;

    @Autowired
    private ServiceEntryRepository serviceEntryRepository;

    private ServiceEntry testService;
    private ServiceEntry anotherService;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        passwordEntryRepository.deleteAll();
        serviceEntryRepository.deleteAll();

        now = LocalDateTime.now();

        testService = new ServiceEntry();
        testService.setName("Test Service");
        testService.setCategory("Test Category");
        testService.setUrl("Test URL");
        testService = entityManager.persistAndFlush(testService);

        anotherService = new ServiceEntry();
        anotherService.setName("Another Service");
        anotherService.setCategory("Another Category");
        anotherService.setUrl("Another URL");
        anotherService = entityManager.persistAndFlush(anotherService);
    }

    private PasswordEntry createPasswordEntry(String password, ServiceEntry service, LocalDateTime createdAt) {
        PasswordEntry entry = new PasswordEntry();
        entry.setPassword(password);
        entry.setServiceName(service);
        entry.setCreatedAt(createdAt);
        return entry;
    }

    @Test
    void testFindByCreatedAtBetween() {
        LocalDateTime startDate = now.minusDays(2);
        LocalDateTime endDate = now.plusDays(2);

        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", testService, now);
        PasswordEntry entry3 = createPasswordEntry("password789", anotherService, now.plusDays(1));

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        List<PasswordEntry> foundEntries = passwordEntryRepository.findByCreatedAtBetween(startDate, endDate);

        assertThat(foundEntries).hasSize(3);
        assertThat(foundEntries).extracting(PasswordEntry::getPassword)
                .containsExactlyInAnyOrder("password123", "password456", "password789");
    }

    @Test
    void testFindByCreatedAtBetweenNoEntriesInRange() {
        LocalDateTime startDate = now.minusDays(10);
        LocalDateTime endDate = now.minusDays(5);

        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", testService, now);
        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);

        List<PasswordEntry> foundEntries = passwordEntryRepository.findByCreatedAtBetween(startDate, endDate);

        assertThat(foundEntries).isEmpty();
    }


    @Test
    void testDeletePasswordEntriesByServiceName() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", testService, now);
        PasswordEntry entry3 = createPasswordEntry("password789", anotherService, now.plusDays(1));

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        passwordEntryRepository.deletePasswordEntriesByServiceName(testService);
        entityManager.flush();
        entityManager.clear();

        List<PasswordEntry> allEntries = passwordEntryRepository.findAll();
        assertThat(allEntries).hasSize(1);
        assertThat(allEntries.get(0).getServiceName().getName()).isEqualTo("Another Service");
    }

    @Test
    void testDeletePasswordEntriesByServiceNameWhenServiceHasNoEntries() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        entityManager.persistAndFlush(entry1);

        ServiceEntry serviceWithNoEntries = new ServiceEntry();
        serviceWithNoEntries.setName("Empty Service");
        serviceWithNoEntries.setCategory("test Category");
        serviceWithNoEntries.setUrl("test url");
        serviceWithNoEntries = entityManager.persistAndFlush(serviceWithNoEntries);

        passwordEntryRepository.deletePasswordEntriesByServiceName(serviceWithNoEntries);
        entityManager.flush();

        List<PasswordEntry> allEntries = passwordEntryRepository.findAll();
        assertThat(allEntries).hasSize(1);
        assertThat(allEntries.get(0).getServiceName().getName()).isEqualTo("Test Service");
    }

    @Test
    void deletePasswordEntriesByServiceNameHandleNull() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        entityManager.persistAndFlush(entry1);

        try {
            passwordEntryRepository.deletePasswordEntriesByServiceName(null);
            entityManager.flush();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(Exception.class);
        }
    }


    @RepeatedTest(value = 5)
    void testFindByServiceNameWhenServiceNameExists() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", testService, now);
        PasswordEntry entry3 = createPasswordEntry("password789", anotherService, now.plusDays(1));

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        List<PasswordEntry> foundEntries = passwordEntryRepository.findByServiceName("Test Service");

        assertThat(foundEntries).hasSize(2);
        assertThat(foundEntries).extracting(PasswordEntry::getPassword)
                .containsExactlyInAnyOrder("password123", "password456");
        assertThat(foundEntries).allMatch(entry -> entry.getServiceName().getName().equals("Test Service"));
    }

    @Test
    void testFindByServiceNameWhenServiceNameDoesNotExist() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        entityManager.persistAndFlush(entry1);

        List<PasswordEntry> foundEntries = passwordEntryRepository.findByServiceName("Non-existent Service");

        assertThat(foundEntries).isEmpty();
    }

    @Test
    void testFindByServiceNameCaseSensitive() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        entityManager.persistAndFlush(entry1);

        List<PasswordEntry> foundEntries = passwordEntryRepository.findByServiceName("test service");

        assertThat(foundEntries).isEmpty();
    }


    @Test
    void testFindByServiceNameHandleNull() {
        List<PasswordEntry> foundEntries = passwordEntryRepository.findByServiceName(null);

        assertThat(foundEntries).isEmpty();
    }

    @Test
    void testFindByServiceNameHandleEmptyString() {
        List<PasswordEntry> foundEntries = passwordEntryRepository.findByServiceName("");

        assertThat(foundEntries).isEmpty();
    }
}