package ru.matthew.NauJava.service.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.matthew.NauJava.domain.audit.Audit;
import ru.matthew.NauJava.domain.audit.AuditRepository;
import ru.matthew.NauJava.domain.audit.AuditServiceImpl;
import ru.matthew.NauJava.domain.audit.EventType;
import ru.matthew.NauJava.domain.audit.dto.AuditCreateDto;
import ru.matthew.NauJava.domain.audit.dto.AuditResponseDto;
import ru.matthew.NauJava.domain.audit.dto.AuditStatsResponseDto;
import ru.matthew.NauJava.domain.audit.exception.AuditEventNotFoundException;
import ru.matthew.NauJava.domain.audit.mapper.AuditMapper;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.password.PasswordEntryRepository;
import ru.matthew.NauJava.domain.profile.ProfileRepository;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.UserRepository;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.matthew.NauJava.domain.audit.EventType.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private AuditMapper auditMapper;
    @Mock
    private AuditRepository auditRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEntryRepository passwordEntryRepository;
    @Mock
    private ProfileRepository profileRepository;
    @InjectMocks
    private AuditServiceImpl auditService;

    private Long auditId;
    private Audit audit;
    private Audit savedAudit;
    private AuditCreateDto auditCreateDto;
    private AuditResponseDto auditResponseDto;
    private User user;
    private Long userId;

    private User createUser() {
        var user = new User();
        user.setId(userId);
        user.setUsername("username");
        user.setEmail("test@test.ru");
        return user;
    }

    private Audit createAudit(EventType eventType) {
        var event = new Audit();
        event.setEventType(eventType);
        event.setUserAgent("userAgentTest");
        event.setDescription("desc");
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    @BeforeEach
    public void setUp() {
        auditId = 1L;
        userId = 11L;
        user = createUser();
        audit = createAudit(SIGN_IN_USER);
        auditCreateDto = new AuditCreateDto(userId, SIGN_IN_USER, "userAgentTest", "desc");
        auditResponseDto = new AuditResponseDto(auditId, userId, SIGN_IN_USER, "desc", "userAgentTest", LocalDateTime.now());
        savedAudit = createAudit(SIGN_IN_USER);
        savedAudit.setId(auditId);
    }

    @Test
    public void createEvent_SuccessCreateEvent_ReturnAuditResponseDto() {
        when(auditMapper.toAudit(auditCreateDto)).thenReturn(audit);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(auditRepository.save(audit)).thenReturn(savedAudit);
        when(auditMapper.toAuditResponseDto(savedAudit)).thenReturn(auditResponseDto);

        var createdEvent = auditService.createEvent(auditCreateDto);

        Assertions.assertNotNull(createdEvent);
        Assertions.assertEquals(auditId, createdEvent.id());
        Assertions.assertEquals(savedAudit.getEventType(), createdEvent.type());
        Assertions.assertEquals(userId, createdEvent.userId());
    }

    @Test
    public void createEvent_WhenUserNotFound_ReturnUserNotFoundException() {
        when(auditMapper.toAudit(auditCreateDto)).thenReturn(audit);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> auditService.createEvent(auditCreateDto));

        verify(auditRepository, Mockito.never()).save(audit);
        verify(auditMapper, Mockito.never()).toAuditResponseDto(savedAudit);
    }

    @Test
    public void findById_Success_ReturnAuditResponseDto() {
        when(auditRepository.findById(auditId)).thenReturn(Optional.ofNullable(savedAudit));
        when(auditMapper.toAuditResponseDto(savedAudit)).thenReturn(auditResponseDto);

        var foundEvent = auditService.findById(auditId);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(auditId, foundEvent.id());
        Assertions.assertEquals(userId, foundEvent.userId());
        Assertions.assertEquals(savedAudit.getEventType(), foundEvent.type());
    }

    @Test
    public void findById_WhenNotFoundEvent_ReturnAuditEventNotFoundException() {
        when(auditRepository.findById(auditId)).thenReturn(Optional.empty());

        Assertions.assertThrows(AuditEventNotFoundException.class, () -> auditService.findById(auditId));

        verify(auditMapper, Mockito.never()).toAuditResponseDto(savedAudit);
    }

    @Test
    public void findByEventType_Success_ReturnAuditResponseDto() {
        var event1 = createAudit(SIGN_IN_USER);
        event1.setId(1L);
        var event2 = createAudit(SIGN_IN_USER);
        event2.setId(2L);

        event1.setUser(user);
        event2.setUser(user);

        var auditResponseDto1 = new AuditResponseDto(
                event1.getId(), event1.getUser().getId(),
                event1.getEventType(), event1.getDescription(),
                event1.getUserAgent(), event1.getCreatedAt()
        );
        var auditResponseDto2 = new AuditResponseDto(
                event2.getId(), event2.getUser().getId(),
                event2.getEventType(), event2.getDescription(),
                event2.getUserAgent(), event2.getCreatedAt()
        );

        List<Audit> events = List.of(event1, event2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, events.size());

        when(auditRepository.findAllByEventType(SIGN_IN_USER, pageable)).thenReturn(auditPage);
        when(auditMapper.toAuditResponseDto(event1)).thenReturn(auditResponseDto1);
        when(auditMapper.toAuditResponseDto(event2)).thenReturn(auditResponseDto2);

        var foundEvent = auditService.findByEventType(SIGN_IN_USER, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(2, foundEvent.getTotalElements());
        Assertions.assertEquals(1, foundEvent.getTotalPages());
        Assertions.assertEquals(2, foundEvent.getNumberOfElements());

        Assertions.assertEquals(auditResponseDto1.id(), foundEvent.getContent().getFirst().id());
        Assertions.assertEquals(auditResponseDto1.type(), foundEvent.getContent().getFirst().type());
        Assertions.assertEquals(auditResponseDto2.id(), foundEvent.getContent().getLast().id());
        Assertions.assertEquals(auditResponseDto2.type(), foundEvent.getContent().getLast().type());
    }

    @Test
    public void findByEventType_WhenNotFoundEvent_ReturnEmptyPage() {
        List<Audit> events = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, 0);

        when(auditRepository.findAllByEventType(any(EventType.class), eq(pageable))).thenReturn(auditPage);

        var foundEvent = auditService.findByEventType(SIGN_IN_USER, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(0, foundEvent.getTotalElements());
        Assertions.assertEquals(0, foundEvent.getTotalPages());
        Assertions.assertEquals(0, foundEvent.getNumberOfElements());

        verify(auditMapper, Mockito.never()).toAuditResponseDto(savedAudit);
    }

    @Test
    public void findByUserAgent_Success_ReturnAuditResponseDto() {
        String mainAgent = "mainAgent";
        String oAgent = "oAgent";

        var event1 = createAudit(SIGN_IN_USER);
        event1.setId(1L);
        event1.setUserAgent(mainAgent);
        var event2 = createAudit(SIGN_IN_USER);
        event2.setId(2L);
        event2.setUserAgent(oAgent);

        event1.setUser(user);
        event2.setUser(user);

        var auditResponseDto2 = new AuditResponseDto(
                event2.getId(), event2.getUser().getId(),
                event2.getEventType(), event2.getDescription(),
                event2.getUserAgent(), event2.getCreatedAt()
        );

        List<Audit> events = List.of(event2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, events.size());

        when(auditRepository.findAllByUserIdAndUserAgent(userId, oAgent, pageable)).thenReturn(auditPage);
        when(auditMapper.toAuditResponseDto(event2)).thenReturn(auditResponseDto2);

        var foundEvent = auditService.findByUserAgent(userId, oAgent, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(1, foundEvent.getTotalElements());
        Assertions.assertEquals(1, foundEvent.getTotalPages());
        Assertions.assertEquals(1, foundEvent.getNumberOfElements());

        Assertions.assertEquals(auditResponseDto2.id(), foundEvent.getContent().getFirst().id());
        Assertions.assertEquals(auditResponseDto2.type(), foundEvent.getContent().getFirst().type());

        verify(auditMapper, Mockito.never()).toAuditResponseDto(event1);
    }

    @Test
    public void findByUserAgent_WhenNotFoundEvent_ReturnEmptyPage() {
        List<Audit> events = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, 0);
        var userAgent = "some user agent";

        when(auditRepository.findAllByUserIdAndUserAgent(userId, userAgent, pageable)).thenReturn(auditPage);

        var foundEvent = auditService.findByUserAgent(userId, userAgent, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(0, foundEvent.getTotalElements());
        Assertions.assertEquals(0, foundEvent.getTotalPages());
        Assertions.assertEquals(0, foundEvent.getNumberOfElements());

        verify(auditMapper, Mockito.never()).toAuditResponseDto(savedAudit);
    }

    @Test
    public void findByCreatedAt_Success_ReturnAuditResponseDto() {
        var event1 = createAudit(SIGN_IN_USER);
        event1.setId(1L);
        event1.setCreatedAt(LocalDateTime.now().minusHours(1));
        var event2 = createAudit(SIGN_IN_USER);
        event2.setId(2L);
        var foundDate = LocalDateTime.now();
        event2.setCreatedAt(foundDate);

        event1.setUser(user);
        event2.setUser(user);

        var auditResponseDto2 = new AuditResponseDto(
                event2.getId(), event2.getUser().getId(),
                event2.getEventType(), event2.getDescription(),
                event2.getUserAgent(), event2.getCreatedAt()
        );

        List<Audit> events = List.of(event2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, events.size());

        when(auditRepository.findByCreatedAt(foundDate, pageable)).thenReturn(auditPage);
        when(auditMapper.toAuditResponseDto(event2)).thenReturn(auditResponseDto2);

        var foundEvent = auditService.findByCreatedAt(foundDate, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(1, foundEvent.getTotalElements());
        Assertions.assertEquals(1, foundEvent.getTotalPages());
        Assertions.assertEquals(1, foundEvent.getNumberOfElements());

        Assertions.assertEquals(auditResponseDto2.id(), foundEvent.getContent().getFirst().id());
        Assertions.assertEquals(auditResponseDto2.type(), foundEvent.getContent().getFirst().type());

        verify(auditMapper, Mockito.never()).toAuditResponseDto(event1);
    }

    @Test
    public void findByCreatedAt_WhenNotFoundEvent_ReturnEmptyPage() {
        List<Audit> events = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, 0);

        var foundDate = LocalDateTime.now();

        when(auditRepository.findByCreatedAt(foundDate, pageable)).thenReturn(auditPage);

        var foundEvent = auditService.findByCreatedAt(foundDate, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(0, foundEvent.getTotalElements());
        Assertions.assertEquals(0, foundEvent.getTotalPages());
        Assertions.assertEquals(0, foundEvent.getNumberOfElements());

        verify(auditMapper, Mockito.never()).toAuditResponseDto(savedAudit);
    }

    @Test
    public void findByUserId_Success_ReturnAuditResponseDto() {
        var event1 = createAudit(SIGN_IN_USER);
        event1.setId(1L);
        var event2 = createAudit(SIGN_IN_USER);
        event2.setId(2L);

        event1.setUser(user);
        event2.setUser(user);

        var auditResponseDto1 = new AuditResponseDto(
                event1.getId(), event1.getUser().getId(),
                event1.getEventType(), event1.getDescription(),
                event1.getUserAgent(), event1.getCreatedAt()
        );
        var auditResponseDto2 = new AuditResponseDto(
                event2.getId(), event2.getUser().getId()
                , event2.getEventType(), event2.getDescription(),
                event2.getUserAgent(), event2.getCreatedAt()
        );

        List<Audit> events = List.of(event1, event2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, events.size());

        when(auditRepository.findAllByUserId(userId, pageable)).thenReturn(auditPage);
        when(auditMapper.toAuditResponseDto(event2)).thenReturn(auditResponseDto2);
        when(auditMapper.toAuditResponseDto(event1)).thenReturn(auditResponseDto1);

        var foundEvent = auditService.findByUserId(userId, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(2, foundEvent.getTotalElements());
        Assertions.assertEquals(1, foundEvent.getTotalPages());
        Assertions.assertEquals(2, foundEvent.getNumberOfElements());

        Assertions.assertEquals(auditResponseDto1.id(), foundEvent.getContent().getFirst().id());
        Assertions.assertEquals(auditResponseDto1.type(), foundEvent.getContent().getFirst().type());
        Assertions.assertEquals(auditResponseDto2.id(), foundEvent.getContent().getLast().id());
        Assertions.assertEquals(auditResponseDto2.type(), foundEvent.getContent().getLast().type());
    }

    @Test
    public void findByUserId_WhenNotFoundEvent_ReturnEmptyPage() {
        List<Audit> events = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, 0);

        when(auditRepository.findAllByUserId(userId, pageable)).thenReturn(auditPage);

        var foundEvent = auditService.findByUserId(userId, pageable);

        Assertions.assertNotNull(foundEvent);
        Assertions.assertEquals(0, foundEvent.getTotalElements());
        Assertions.assertEquals(0, foundEvent.getTotalPages());
        Assertions.assertEquals(0, foundEvent.getNumberOfElements());
    }

    @Test
    public void findAll_WhenEventsExists_ReturnPaginatedEvents() {
        var event1 = createAudit(CREATE_ENTRY);
        event1.setId(1L);
        var event2 = createAudit(CREATE_ENTRY);
        event2.setId(2L);

        event1.setUser(user);
        event2.setUser(user);

        var eDto1 = new AuditResponseDto(
                event1.getId(), userId, event1.getEventType(),
                event1.getDescription(), event1.getUserAgent(),
                event1.getCreatedAt()
        );
        var eDto2 = new AuditResponseDto(
                event2.getId(), userId,
                event2.getEventType(), event2.getDescription(),
                event2.getUserAgent(), event2.getCreatedAt()
        );

        List<Audit> events = List.of(event1, event2);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, events.size());

        when(auditRepository.findAll(pageable)).thenReturn(auditPage);
        when(auditMapper.toAuditResponseDto(event1)).thenReturn(eDto1);
        when(auditMapper.toAuditResponseDto(event2)).thenReturn(eDto2);

        var foundEvents = auditService.findAll(pageable);

        Assertions.assertNotNull(foundEvents);
        Assertions.assertEquals(2, foundEvents.getTotalElements());
        Assertions.assertEquals(1, foundEvents.getTotalPages());
        Assertions.assertEquals(2, foundEvents.getNumberOfElements());

        List<Long> id = foundEvents.getContent().stream().map(AuditResponseDto::id).toList();
        Assertions.assertEquals(List.of(1L, 2L), id);
    }

    @Test
    public void findAll_WhenEventsDoesNotExists_ReturnPaginatedEvents() {
        List<Audit> events = List.of();
        Pageable pageable = PageRequest.of(0, 2);
        Page<Audit> auditPage = new PageImpl<>(events, pageable, 0);

        when(auditRepository.findAll(pageable)).thenReturn(auditPage);

        var foundEvents = auditService.findAll(pageable);

        Assertions.assertNotNull(foundEvents);
        Assertions.assertEquals(0, foundEvents.getTotalElements());
        Assertions.assertEquals(0, foundEvents.getTotalPages());
        Assertions.assertEquals(0, foundEvents.getNumberOfElements());

        Assertions.assertTrue(foundEvents.getContent().isEmpty());
    }

    @Test
    public void countAllEvent_ReturnNumberOfEvents() {
        var numberOfEvents = 10L;
        when(auditRepository.count()).thenReturn(numberOfEvents);

        long count = auditService.countAllEvent();

        Assertions.assertEquals(numberOfEvents, count);
    }

    @Test
    public void countAllUser_ReturnNumberOfUsers() {
        var numberOfUsers = 10L;
        when(userRepository.count()).thenReturn(numberOfUsers);

        long count = auditService.countAllUser();

        Assertions.assertEquals(numberOfUsers, count);
    }

    @Test
    public void countAllUserForLastDay_ReturnNumberOfUsers() {
        var numberOfUsers = 10L;
        ArgumentCaptor<LocalDateTime> argumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        when(userRepository.countByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(numberOfUsers);

        long count = auditService.countAllUserForLastDay();

        Assertions.assertEquals(numberOfUsers, count);
        verify(userRepository).countByCreatedAtAfter(argumentCaptor.capture());

        var capturedDate = argumentCaptor.getValue();
        var expectedDate = LocalDateTime.now().minusDays(1);

        long differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(expectedDate, capturedDate));
        Assertions.assertTrue(differenceInSeconds <= 1);
    }

    @Test
    public void countAllPasswordEntries_ReturnNumberOfEntries() {
        var numberOfEntries = 10L;
        when(passwordEntryRepository.count()).thenReturn(numberOfEntries);

        long count = auditService.countAllPasswordEntries();

        Assertions.assertEquals(numberOfEntries, count);
    }

    @Test
    public void getMostPopularCipher_ReturnStringNameCipher() {
        var popularCipher = List.of(CipherAlgorithmSpec.AES);
        Pageable pageable = PageRequest.of(0, 1);
        when(profileRepository.findMostPopularAlgorithm(pageable)).thenReturn(popularCipher);

        var cipherName = auditService.getMostPopularCipher();

        Assertions.assertNotNull(cipherName);
        Assertions.assertEquals(CipherAlgorithmSpec.AES.getName(), cipherName);
    }

    @Test
    public void getMostPopularCipher_WhenListEmpty_ReturnStringNameCipher() {
        List<CipherAlgorithmSpec> popularCipher = List.of();
        Pageable pageable = PageRequest.of(0, 1);
        when(profileRepository.findMostPopularAlgorithm(pageable)).thenReturn(popularCipher);

        Assertions.assertThrows(NoSuchElementException.class, () -> auditService.getMostPopularCipher());
    }

    @Test
    public void getAllStatsSystem_ReturnAuditStatsResponseDto() {
        var numberOfEvents = 10L;
        var numberOfUsers = 10L;
        var numberOfEntries = 10L;
        var popularCipher = List.of(CipherAlgorithmSpec.AES);
        var auditStatsResponseDto = new AuditStatsResponseDto(numberOfEvents, numberOfUsers,
                numberOfEntries, numberOfUsers, CipherAlgorithmSpec.AES.getName()
        );
        Pageable pageable = PageRequest.of(0, 1);

        when(profileRepository.findMostPopularAlgorithm(pageable)).thenReturn(popularCipher);
        when(passwordEntryRepository.count()).thenReturn(numberOfEntries);
        ArgumentCaptor<LocalDateTime> argumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        when(userRepository.countByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(numberOfUsers);
        when(userRepository.count()).thenReturn(numberOfUsers);
        when(auditRepository.count()).thenReturn(numberOfEvents);
        when(auditMapper.toAuditStatsResponseDto(
                numberOfEvents, numberOfUsers,
                numberOfEntries, numberOfUsers,
                CipherAlgorithmSpec.AES.getName()
        )).thenReturn(auditStatsResponseDto);

        var stats = auditService.getAllStatsSystem();

        Assertions.assertNotNull(stats);
        Assertions.assertEquals(numberOfEvents, stats.totalEvent());
        Assertions.assertEquals(numberOfUsers, stats.totalUser());
        Assertions.assertEquals(numberOfEntries, stats.totalEntries());
        Assertions.assertEquals(numberOfUsers, stats.totalUsersForLastDay());
        Assertions.assertEquals(CipherAlgorithmSpec.AES.getName(), stats.popularCipher());

        verify(userRepository).countByCreatedAtAfter(argumentCaptor.capture());

        var capturedDate = argumentCaptor.getValue();
        var expectedDate = LocalDateTime.now().minusDays(1);

        long differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(expectedDate, capturedDate));
        Assertions.assertTrue(differenceInSeconds <= 1);
    }

    @Test
    public void deleteById_WhenEventsExists_ShouldDeleteTagetEvent() {
        auditService.deleteById(auditId);

        verify(auditRepository).deleteById(auditId);
    }

    @Test
    public void deleteByUserId_WhenEventsExists_ShouldDeleteTagetEvents() {
        auditService.deleteByUserId(userId);

        verify(auditRepository).deleteAllByUserId(userId);
    }

    @Test
    public void deleteByEventType_WhenEventsExists_ShouldDeleteTagetEvents() {
        var event = SIGN_IN_USER;

        auditService.deleteByEventType(event);

        verify(auditRepository).deleteAllByEventType(event);
    }

    @Test
    public void deleteByCreatedAt_WhenEventsExists_ShouldDeleteTagetEvents() {
        var dateToDelete = LocalDateTime.now().minusDays(2);

        auditService.deleteByCreatedAt(dateToDelete);

        verify(auditRepository).deleteByCreatedAt(dateToDelete);
    }
}
