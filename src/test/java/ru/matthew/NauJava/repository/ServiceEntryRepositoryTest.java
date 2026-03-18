package ru.matthew.NauJava.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.matthew.NauJava.entity.ServiceEntry;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServiceEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceEntryRepository serviceEntryRepository;

    private ServiceEntry createTestServiceEntry(String name, String category, String url) {
        ServiceEntry entry = new ServiceEntry();
        entry.setName(name);
        entry.setCategory(category);
        entry.setUrl(url);
        return entry;
    }

    @Test
    void testFindServiceEntryByNameExists() {
        ServiceEntry savedEntry = entityManager.persistAndFlush(
                createTestServiceEntry("Test Service", "Test Description", "Test URL")
        );

        Optional<ServiceEntry> found = serviceEntryRepository.findServiceEntryByName("Test Service");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedEntry.getId());
        assertThat(found.get().getName()).isEqualTo("Test Service");
    }

    @Test
    void testFindServiceEntryByNameNotExist() {
        Optional<ServiceEntry> found = serviceEntryRepository.findServiceEntryByName("Non-existent Service");

        assertThat(found).isEmpty();
    }

    @Test
    void testFndServiceEntryByNameCaseSensitive() {
        entityManager.persistAndFlush(
                createTestServiceEntry("Test Service", "Test Category", "Test URL")
        );

        Optional<ServiceEntry> found = serviceEntryRepository.findServiceEntryByName("test service");

        assertThat(found).isEmpty();
    }

    @Test
    void testFindServiceEntryByNameReturnCorrectEntry() {
        entityManager.persistAndFlush(
                createTestServiceEntry("Service 1", "category 1", "url 1")
        );
        entityManager.persistAndFlush(
                createTestServiceEntry("Service 2", "category 2", "url 2")
        );
        ServiceEntry expectedEntry = entityManager.persistAndFlush(
                createTestServiceEntry("Target Service", "Target category", "Target url")
        );

        Optional<ServiceEntry> found = serviceEntryRepository.findServiceEntryByName("Target Service");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(expectedEntry.getId());
        assertThat(found.get().getName()).isEqualTo("Target Service");
    }

    @Test
    void testFindServiceEntryByNameNull() {
        Optional<ServiceEntry> found = serviceEntryRepository.findServiceEntryByName(null);

        assertThat(found).isEmpty();
    }

    @Test
    void testFindServiceEntryByNameEmptyString() {
        Optional<ServiceEntry> found = serviceEntryRepository.findServiceEntryByName("");

        assertThat(found).isEmpty();
    }

}