package ru.matthew.NauJava.service.profile;

import ru.matthew.NauJava.entity.GeneratorProfile;

public interface GeneratorProfileService {

    /**
     * Создает конфигурацию-генерации паролей для конкретного пользователя
     *
     * @param userId идентификатор пользователя
     * @param profile профайл-генерации паролей
     */
    void saveGeneratorProfile(Long userId, GeneratorProfile profile);
}
