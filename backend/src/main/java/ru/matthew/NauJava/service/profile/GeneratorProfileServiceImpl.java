package ru.matthew.NauJava.service.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import ru.matthew.NauJava.entity.AuditLog;
import ru.matthew.NauJava.entity.EventType;
import ru.matthew.NauJava.entity.GeneratorProfile;
import ru.matthew.NauJava.entity.User;
import ru.matthew.NauJava.repository.exception.UserNotFoundException;
import ru.matthew.NauJava.repository.AuditLogRepository;
import ru.matthew.NauJava.repository.GeneratorProfileRepository;
import ru.matthew.NauJava.repository.UserRepository;


@Service
public class GeneratorProfileServiceImpl implements GeneratorProfileService {

    private final UserRepository userRepository;
    private final GeneratorProfileRepository generatorProfileRepository;
    private final PlatformTransactionManager transactionManager;
    private final AuditLogRepository auditLogRepository;

    private static final String CREATE_PROFILE_MESSAGE = "Создание профайла-генерации паролей '%s'";
    private static final String NOT_FOUND_USER = "Пользователя не удалось найти!";

    @Autowired
    public GeneratorProfileServiceImpl(UserRepository userRepository,
                                       GeneratorProfileRepository generatorProfileRepository,
                                       AuditLogRepository auditLogRepository,
                                       PlatformTransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.generatorProfileRepository = generatorProfileRepository;
        this.auditLogRepository = auditLogRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    @Transactional
    public void saveGeneratorProfile(Long userId, GeneratorProfile profile) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new UserNotFoundException(NOT_FOUND_USER)
            );
            profile.setUser(user);
            generatorProfileRepository.save(profile);

            user.addProfile(profile);

            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(EventType.CREATE);
            auditLog.setEventDescription(CREATE_PROFILE_MESSAGE.formatted(profile.getName()));
            auditLogRepository.save(auditLog);

            transactionManager.commit(status);
        } catch (DataAccessException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
