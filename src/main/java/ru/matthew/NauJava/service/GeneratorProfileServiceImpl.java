package ru.matthew.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.matthew.NauJava.entity.GeneratorProfile;
import ru.matthew.NauJava.entity.User;
import ru.matthew.NauJava.exception.UserNotFoundException;
import ru.matthew.NauJava.repository.GeneratorProfileRepository;
import ru.matthew.NauJava.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class GeneratorProfileServiceImpl implements GeneratorProfileService {

    private final UserRepository userRepository;
    private final GeneratorProfileRepository generatorProfileRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public GeneratorProfileServiceImpl(UserRepository userRepository,
                                       GeneratorProfileRepository generatorProfileRepository,
                                       TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.generatorProfileRepository = generatorProfileRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public GeneratorProfile createProfileForUser(Long userId, GeneratorProfile profileData) {
        return transactionTemplate.execute(status -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Пользователя не удалось найти!"));

            GeneratorProfile profile = new GeneratorProfile();
            profile.setName(profileData.getName());
            profile.setPasswordLength(profileData.getPasswordLength());
            profile.setUppercase(profileData.isUppercase());
            profile.setLowercase(profileData.isLowercase());
            profile.setDigits(profileData.isDigits());
            profile.setSpecialChars(profileData.isSpecialChars());
            profile.setAvoidAmbiguousChars(profileData.isAvoidAmbiguousChars());
            profile.setCustomChars(profileData.getCustomChars());
            profile.setCreateAt(LocalDateTime.now());
            profile.setUser(user);

            return generatorProfileRepository.save(profile);
        });
    }
}
