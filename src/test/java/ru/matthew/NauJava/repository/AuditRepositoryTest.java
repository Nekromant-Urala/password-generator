package ru.matthew.NauJava.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.matthew.NauJava.domain.audit.Audit;
import ru.matthew.NauJava.domain.audit.AuditRepository;
import ru.matthew.NauJava.domain.audit.EventType;
import ru.matthew.NauJava.domain.user.Role;
import ru.matthew.NauJava.domain.user.User;

import java.time.LocalDateTime;

import static ru.matthew.NauJava.domain.audit.EventType.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AuditRepositoryTest {

    private User user;
    private AuditRepository auditRepository;
    private TestEntityManager entityManager;

    @Autowired
    public AuditRepositoryTest(AuditRepository auditRepository, TestEntityManager entityManager) {
        this.auditRepository = auditRepository;
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

    private Audit createEvent(EventType eventType, String userAgent) {
        var event = new Audit();
        event.setEventType(eventType);
        event.setUserAgent(userAgent);
        event.setDescription("desc");
        return event;
    }

    @BeforeEach
    public void setUp() {
        entityManager.clear();

        user = createUser("username", "email@test.ru");
        entityManager.persistAndFlush(user);
    }

    @Test
    public void findAllByEventType_WhenEventTypeExists_ReturnPaginatedEvent() {
        var event1 = createEvent(CREATE_ENTRY, "userAgent");
        var event2 = createEvent(CREATE_ENTRY, "userAgent");
        var event3 = createEvent(CREATE_ENTRY, "userAgent");
        var event4 = createEvent(LOGOUT_USER, "userAgent");
        var pageable = PageRequest.of(0, 2, Sort.by("eventType").ascending());

        event1.setUser(user);
        event2.setUser(user);
        event3.setUser(user);
        event4.setUser(user);

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.flush();
        entityManager.clear();

        var foundEvents = auditRepository.findAllByEventType(CREATE_ENTRY, pageable);

        Assertions.assertThat(foundEvents).isNotNull();

        Assertions.assertThat(foundEvents.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(foundEvents.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(foundEvents.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(foundEvents.hasNext()).isTrue();
        Assertions.assertThat(foundEvents.hasPrevious()).isFalse();
    }

    @Test
    public void findAllByUserIdAndUserAgent_WhenDifferentUserAgent_ReturnPaginatedEvent() {
        var event1 = createEvent(CREATE_ENTRY, "userAgent1");
        var event2 = createEvent(CREATE_ENTRY, "userAgent2");
        var event3 = createEvent(CREATE_ENTRY, "userAgent2");
        var event4 = createEvent(CREATE_ENTRY, "userAgent2");
        var event5 = createEvent(LOGOUT_USER, "userAgent1");
        var pageable = PageRequest.of(0, 2, Sort.by("userAgent").ascending());

        event1.setUser(user);
        event2.setUser(user);
        event3.setUser(user);
        event4.setUser(user);
        event5.setUser(user);

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.persist(event5);
        entityManager.flush();
        entityManager.clear();

        var foundEvents = auditRepository.findAllByUserIdAndUserAgent(user.getId(), event2.getUserAgent(), pageable);

        Assertions.assertThat(foundEvents).isNotNull();

        Assertions.assertThat(foundEvents.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(foundEvents.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(foundEvents.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(foundEvents.hasNext()).isTrue();
        Assertions.assertThat(foundEvents.hasPrevious()).isFalse();
    }

    @Test
    public void findByCreatedAt_ReturnPaginatedEvent() {
        var event1 = createEvent(SIGN_IN_USER, "userAgent1");
        var event2 = createEvent(CREATE_ENTRY, "userAgent1");
        var event3 = createEvent(LOGOUT_USER, "userAgent1");
        var pageable = PageRequest.of(0, 2, Sort.by("createdAt").ascending());

        event1.setUser(user);
        event2.setUser(user);
        event3.setUser(user);

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        var exactDbTime = event2.getCreatedAt();
        entityManager.flush();
        entityManager.clear();

        var foundEvent = auditRepository.findByCreatedAt(exactDbTime, pageable);

        Assertions.assertThat(foundEvent).isNotNull();

        Assertions.assertThat(foundEvent.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(foundEvent.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(foundEvent.getNumberOfElements()).isEqualTo(1);

        Assertions.assertThat(foundEvent.hasNext()).isFalse();
        Assertions.assertThat(foundEvent.hasPrevious()).isFalse();
    }

    @Test
    public void findAllByUserId_ReturnPaginatedEvents() {
        var event1 = createEvent(SIGN_IN_USER, "userAgent1");
        var event2 = createEvent(CREATE_ENTRY, "userAgent1");
        var event3 = createEvent(LOGOUT_USER, "userAgent1");

        event1.setUser(user);
        event2.setUser(user);
        event3.setUser(user);

        var oUser = createUser("oUser", "tes.tes@tew.tu");
        var event4 = createEvent(SIGN_IN_USER, "userAgent2");
        var event5 = createEvent(CREATE_PROFILE, "userAgent2");
        var event6 = createEvent(LOGOUT_USER, "userAgent2");
        var pageable = PageRequest.of(0, 2, Sort.by("userAgent").ascending());

        event4.setUser(oUser);
        event5.setUser(oUser);
        event6.setUser(oUser);

        entityManager.persist(oUser);
        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.persist(event5);
        entityManager.persist(event6);


        entityManager.flush();
        entityManager.clear();

        var foundEvents = auditRepository.findAllByUserId(oUser.getId(), pageable);

        Assertions.assertThat(foundEvents).isNotNull();

        Assertions.assertThat(foundEvents.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(foundEvents.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(foundEvents.getNumberOfElements()).isEqualTo(2);

        Assertions.assertThat(foundEvents.hasNext()).isTrue();
        Assertions.assertThat(foundEvents.hasPrevious()).isFalse();
    }

    @Test
    public void deleteAllByUserId_ShouldDeleteOnlyTargetUserEvents() {
        var event1 = createEvent(SIGN_IN_USER, "userAgent1");
        var event2 = createEvent(CREATE_ENTRY, "userAgent1");
        var event22 = createEvent(CREATE_ENTRY, "userAgent1");
        var event3 = createEvent(LOGOUT_USER, "userAgent1");
        event1.setUser(user);
        event22.setUser(user);
        event2.setUser(user);
        event3.setUser(user);

        var oUser = createUser("oUser", "tes.tes@tew.tu");
        var event4 = createEvent(SIGN_IN_USER, "userAgent2");
        var event5 = createEvent(CREATE_PROFILE, "userAgent2");
        var event6 = createEvent(LOGOUT_USER, "userAgent2");
        event4.setUser(oUser);
        event5.setUser(oUser);
        event6.setUser(oUser);

        entityManager.persist(oUser);
        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event22);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.persist(event5);
        entityManager.persist(event6);

        entityManager.flush();
        entityManager.clear();

        auditRepository.deleteAllByUserId(oUser.getId());
        var foundEvents = auditRepository.findAll();

        Assertions.assertThat(foundEvents).isNotNull();
        Assertions.assertThat(foundEvents.size()).isEqualTo(4);
        Assertions.assertThat(foundEvents).extracting(Audit::getEventType).containsExactly(SIGN_IN_USER, CREATE_ENTRY, CREATE_ENTRY, LOGOUT_USER);
    }

    @Test
    public void deleteAllByEventType_ShouldDeleteEvents() {
        var event1 = createEvent(SIGN_IN_USER, "userAgent1");
        var event2 = createEvent(CREATE_ENTRY, "userAgent1");
        var event3 = createEvent(CREATE_ENTRY, "userAgent1");
        event1.setUser(user);
        event2.setUser(user);
        event3.setUser(user);

        var oUser = createUser("oUser", "tes.tes@tew.tu");
        var event4 = createEvent(SIGN_IN_USER, "userAgent2");
        var event5 = createEvent(CREATE_PROFILE, "userAgent2");
        event4.setUser(oUser);
        event5.setUser(oUser);

        entityManager.persist(oUser);
        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.persist(event5);


        entityManager.flush();
        entityManager.clear();

        auditRepository.deleteAllByEventType(CREATE_ENTRY);
        var foundEvents = auditRepository.findAll();

        Assertions.assertThat(foundEvents).isNotNull();
        Assertions.assertThat(foundEvents.size()).isEqualTo(3);
        Assertions.assertThat(foundEvents).extracting(Audit::getEventType).containsExactly(SIGN_IN_USER, SIGN_IN_USER, CREATE_PROFILE);
    }

    @Test
    public void deleteByCreatedAt_ShouldDeleteEvents() {
        var event1 = createEvent(SIGN_IN_USER, "userAgent1");
        var event2 = createEvent(CREATE_ENTRY, "userAgent1");
        var event3 = createEvent(LOGOUT_USER, "userAgent1");

        event1.setUser(user);
        event2.setUser(user);
        event3.setUser(user);

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        var exactDbTime = event2.getCreatedAt();
        entityManager.flush();
        entityManager.clear();

        auditRepository.deleteByCreatedAt(exactDbTime);
        var foundEvents = auditRepository.findAll();

        Assertions.assertThat(foundEvents).isNotNull();
        Assertions.assertThat(foundEvents.size()).isEqualTo(2);
        Assertions.assertThat(foundEvents).extracting(Audit::getEventType).containsExactly(SIGN_IN_USER, LOGOUT_USER);
    }

    @Test
    public void deleteByUserIdAndUserAgent_ShouldDeleteEvents() {
        var event1 = createEvent(SIGN_IN_USER, "userAgent1");
        var event2 = createEvent(CREATE_ENTRY, "userAgent1");
        var event3 = createEvent(CREATE_ENTRY, "userAgent1");
        var event4 = createEvent(SIGN_IN_USER, "userAgent2");
        var event5 = createEvent(CREATE_PROFILE, "userAgent2");
        event1.setUser(user);
        event2.setUser(user);
        event3.setUser(user);
        event4.setUser(user);
        event5.setUser(user);

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.persist(event3);
        entityManager.persist(event4);
        entityManager.persist(event5);

        entityManager.flush();
        entityManager.clear();

        auditRepository.deleteByUserIdAndUserAgent(user.getId(), event1.getUserAgent());
        var foundEvents = auditRepository.findAll();

        Assertions.assertThat(foundEvents).isNotNull();
        Assertions.assertThat(foundEvents.size()).isEqualTo(2);
        Assertions.assertThat(foundEvents).extracting(Audit::getEventType).containsExactly(SIGN_IN_USER, CREATE_PROFILE);
    }
}
