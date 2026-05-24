package ru.matthew.NauJava.domain.crypto.generation;

import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileForPasswordDto;

public interface RandomGeneratorService {

    /**
     * Генерирует пароля по заданным настройкам пользователя
     *
     * @param dto данные для генерации пароля {@link GeneratorProfileForPasswordDto}
     * @return Возвращает сгенерированный пароль в виде массива символов (char[])
     */
    char[] generatePassword(GeneratorProfileForPasswordDto dto);
}
