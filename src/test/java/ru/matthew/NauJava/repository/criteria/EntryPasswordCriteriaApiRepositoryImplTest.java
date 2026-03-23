package ru.matthew.NauJava.repository.criteria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import ru.matthew.NauJava.entity.PasswordEntry;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "ru.matthew.NauJava.repository.criteria")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EntryPasswordCriteriaApiRepositoryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EntryPasswordCriteriaApiRepository repository;

    private LocalDateTime nowTime;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        nowTime = LocalDateTime.now();

    }

    private PasswordEntry createPasswordEntry(String password, String nameService, LocalDateTime createdAt) {
        PasswordEntry entry = new PasswordEntry();
        entry.setPassword(password);
        entry.setCreatedAt(createdAt);
        entry.setServiceName(nameService);
        return entry;
    }

    @Test
    @DisplayName("успешное нахождение всех записей в заданном промежутке времени")
    void testFindByCreatedAtBetween_Success() {
        LocalDateTime startDate = nowTime.minusDays(2);
        LocalDateTime endDate = nowTime.plusDays(2);

        PasswordEntry entry1 = createPasswordEntry("password123", "testService", nowTime.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", "testService", nowTime);
        PasswordEntry entry3 = createPasswordEntry("password789", "testService", nowTime.plusDays(8));

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        List<PasswordEntry> foundEntries = repository.findByCreatedAtBetween(startDate, endDate);

        assertEquals(2, foundEntries.size());
        List<PasswordEntry> password = List.of(entry1, entry2);
        assertTrue(foundEntries.containsAll(password));
    }

    @Test
    @DisplayName("отсутствие записей в заданном промежутке")
    void testFindByCreatedAtBetween_NoEntriesInRange() {
        LocalDateTime startDate = nowTime.minusDays(10);
        LocalDateTime endDate = nowTime.minusDays(5);

        PasswordEntry entry1 = createPasswordEntry("password123", "testService", nowTime.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", "testService", nowTime);
        PasswordEntry entry3 = createPasswordEntry("password789", "testService", nowTime.plusDays(1));

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        List<PasswordEntry> foundEntries = repository.findByCreatedAtBetween(startDate, endDate);

        assertEquals(0, foundEntries.size());
        List<PasswordEntry> password = List.of(entry1, entry2, entry3);
        assertFalse(foundEntries.containsAll(password));
    }

    @RepeatedTest(value = 5)
    @DisplayName("нахождение записей по существующему имени сервиса")
    void testFindByServiceName_WhenServiceNameExists() {
        PasswordEntry entry1 = createPasswordEntry("password123", "testService", nowTime.minusDays(1));
        PasswordEntry entry2 = createPasswordEntry("password456", "testService", nowTime);
        PasswordEntry entry3 = createPasswordEntry("password789", "testService_2", nowTime.plusDays(1));

        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.persistAndFlush(entry3);

        List<PasswordEntry> foundEntries = repository.findByServiceName("testService");

        assertEquals(2, foundEntries.size());
        List<PasswordEntry> entries = List.of(entry1, entry2);
        assertTrue(foundEntries.containsAll(entries));
    }

    @Test
    @DisplayName("нахождение записей по не существующему имени сервиса")
    void testFindByServiceName_WhenServiceName_DoesNotExist() {
        PasswordEntry entry1 = createPasswordEntry("password123", "testService", nowTime.minusDays(1));
        entityManager.persistAndFlush(entry1);

        List<PasswordEntry> foundEntries = repository.findByServiceName("ServiceNameDoesNotExist");

        assertTrue(foundEntries.isEmpty());
    }

    @Test
    @DisplayName("нахождение записей по чувствительному к регистру имени сервиса")
    void testFindByServiceName_CaseSensitive() {
        PasswordEntry entry1 = createPasswordEntry("password123", "testService", nowTime.minusDays(1));
        entityManager.persistAndFlush(entry1);

        List<PasswordEntry> foundEntries = repository.findByServiceName("TestService");

        assertTrue(foundEntries.isEmpty());
    }


    @Test
    @DisplayName("обработка случая null в качестве имени сервиса")
    void testFindByServiceName_HandleNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.findByServiceName(null)
        );
        assertEquals("Некорректное название сервиса!", exception.getMessage());
    }

    @Test
    @DisplayName("обработка случая пустой строки в качестве имени сервиса")
    void testFindByServiceName_HandleEmptyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.findByServiceName("")
        );
        assertEquals("Некорректное название сервиса!", exception.getMessage());
    }
}