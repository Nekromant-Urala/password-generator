package ru.matthew.NauJava.repository.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import ru.matthew.NauJava.entity.PasswordEntry;
import ru.matthew.NauJava.entity.ServiceEntry;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "ru.matthew.NauJava.repository.criteria")
class EntryPasswordCriteriaApiRepositoryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EntryPasswordCriteriaApiRepository repository;

    private ServiceEntry testService;
    private ServiceEntry anotherService;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        entityManager.clear();

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

        List<PasswordEntry> foundEntries = repository.findByCreatedAtBetween(startDate, endDate);

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

        List<PasswordEntry> foundEntries = repository.findByCreatedAtBetween(startDate, endDate);

        assertThat(foundEntries).isEmpty();
    }

    @RepeatedTest(value = 5)
    void testFindByServiceNameWhenServiceNameExists() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", testService, now);
        PasswordEntry entry3 = createPasswordEntry("password789", anotherService, now.plusDays(1));

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        List<PasswordEntry> foundEntries = repository.findByServiceName("Test Service");

        assertThat(foundEntries).hasSize(2);
        assertThat(foundEntries).extracting(PasswordEntry::getPassword)
                .containsExactlyInAnyOrder("password123", "password456");
        assertThat(foundEntries).allMatch(entry -> entry.getServiceName().getName().equals("Test Service"));
    }

    @Test
    void testFindByServiceNameWhenServiceNameDoesNotExist() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        entityManager.persistAndFlush(entry1);

        List<PasswordEntry> foundEntries = repository.findByServiceName("Non-existent Service");

        assertThat(foundEntries).isEmpty();
    }

    @Test
    void testFindByServiceNameCaseSensitive() {
        PasswordEntry entry1 = createPasswordEntry("password123", testService, now.minusDays(1));
        entityManager.persistAndFlush(entry1);

        List<PasswordEntry> foundEntries = repository.findByServiceName("test service");

        assertThat(foundEntries).isEmpty();
    }


    @Test
    void testFindByServiceNameHandleNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.findByServiceName(null)
        );
        assertEquals("Некорректное название сервиса!", exception.getMessage());
    }

    @Test
    void testFindByServiceNameHandleEmptyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.findByServiceName("")
        );
        assertEquals("Некорректное название сервиса!", exception.getMessage());
    }
}