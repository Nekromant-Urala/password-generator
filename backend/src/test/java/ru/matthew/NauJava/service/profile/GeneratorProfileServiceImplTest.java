package ru.matthew.NauJava.service.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import ru.matthew.NauJava.domain.audit.AuditLog;
import ru.matthew.NauJava.domain.profile.GeneratorProfile;
import ru.matthew.NauJava.domain.profile.GeneratorProfileServiceImpl;
import ru.matthew.NauJava.domain.user.User;
import ru.matthew.NauJava.domain.user.exception.UserNotFoundException;
import ru.matthew.NauJava.domain.audit.AuditLogRepository;
import ru.matthew.NauJava.domain.profile.GeneratorProfileRepository;
import ru.matthew.NauJava.domain.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class GeneratorProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeneratorProfileRepository generatorProfileRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TransactionStatus status;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private GeneratorProfileServiceImpl profileService;

    private Long userId;
    private User testUser;
    private GeneratorProfile profile;

    @BeforeEach
    void setUp() {
        userId = 1L;
        profile = new GeneratorProfile();
        profile.setName("testProfile");
        profile.setPasswordLength(123);
        profile.setDigits(true);

        testUser = new User();
        testUser.setId(userId);
    }

    @Test
    @DisplayName("успешного добавления профайла")
    void testSaveGeneratorProfile_Success() {
        when(transactionManager.getTransaction(any())).thenReturn(status);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        profileService.saveGeneratorProfile(userId, profile);

        verify(generatorProfileRepository).save(profile);
        verify(auditLogRepository).save(any(AuditLog.class));
        verify(transactionManager, never()).rollback(any());
    }

    @Test
    @DisplayName("отсутствие пользователя при поиске и соответствующее выбрасывание ошибки")
    void testSaveGeneratorProfile_UserNotFound_ThrowsException() {

        when(transactionManager.getTransaction(any())).thenReturn(status);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                profileService.saveGeneratorProfile(userId, profile)
        );
    }

    @Test
    @DisplayName("проверка отката транзакции при возникновении ошибки")
    void testSaveGeneratorProfile_RollbackOnException() {
        when(transactionManager.getTransaction(any())).thenReturn(status);
        when(userRepository.findById(anyLong())).thenThrow(new DataAccessException("some kind of Error DB") {
        });

        assertThrows(DataAccessException.class, () ->
                profileService.saveGeneratorProfile(userId, profile)
        );
        verify(transactionManager).rollback(status);
    }

    @Test
    @DisplayName("проверка отката транзакции при возникновении ошибки в AuditLog")
    void testSaveGeneratorProfile_WhenAuditLogFails_ShouldRollback() {

        when(transactionManager.getTransaction(any())).thenReturn(status);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        doThrow(new DataAccessException("some kind of Error AuditLog") {
        })
                .when(auditLogRepository).save(any(AuditLog.class));

        assertThrows(DataAccessException.class, () ->
                profileService.saveGeneratorProfile(userId, profile)
        );

        verify(transactionManager).rollback(status);
    }
}
