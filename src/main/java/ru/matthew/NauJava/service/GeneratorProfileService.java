package ru.matthew.NauJava.service;

import ru.matthew.NauJava.entity.GeneratorProfile;
import ru.matthew.NauJava.entity.User;

public interface GeneratorProfileService {

    /**
     * Сохраняет конфигурацию создания
     *
     * @param userId           идентификатор пользователя
     * @param generatorProfile профайл генерации паролей
     * @return возвращает созданный профайл
     */
    GeneratorProfile createProfileForUser(Long userId, GeneratorProfile generatorProfile);
}
