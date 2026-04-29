package ru.matthew.NauJava.domain.profile;

public interface GeneratorProfileService {

    /**
     * Создает конфигурацию-генерации паролей для конкретного пользователя
     *
     * @param userId идентификатор пользователя
     * @param profile профайл-генерации паролей
     */
    void saveGeneratorProfile(Long userId, GeneratorProfile profile);
}
