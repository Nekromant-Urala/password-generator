package ru.matthew.NauJava.domain.profile;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileCreateDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileResponseDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileSettingsDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для работы с профилями генерации.
 * Управляет настройками, алгоритмами формирования ключей (KDF) и шифрования.
 */
public interface GeneratorProfileService {

    /**
     * Создает новый профиль генерации.
     *
     * @param dto данные для создания профиля.
     * @return Созданный профиль {@link GeneratorProfileResponseDto}.
     */
    GeneratorProfileResponseDto createGenerateProfile(GeneratorProfileCreateDto dto);

    /**
     * Поиск профиля по уникальному идентификатору.
     *
     * @param id уникальный идентификатор.
     * @return Найденный профиль {@link GeneratorProfileResponseDto}.
     */
    GeneratorProfileResponseDto findById(Long id);

    /**
     * Поиск профилей по их названию.
     *
     * @param name название профиля.
     * @return Список профилей {@link GeneratorProfileResponseDto} с совпадающим названием.
     */
    List<GeneratorProfileResponseDto> findByName(String name);

    /**
     * Поиск профилей, созданных в заданном временном диапазоне.
     *
     * @param startDate начальная дата диапазона.
     * @param endDate   конечная дата диапазона.
     * @return Список профилей {@link GeneratorProfileResponseDto}.
     */
    List<GeneratorProfileResponseDto> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Поиск профилей по точной дате создания.
     *
     * @param createAt дата создания.
     * @return Список профилей {@link GeneratorProfileResponseDto}.
     */
    List<GeneratorProfileResponseDto> findByCreateAt(LocalDateTime createAt);

    /**
     * Поиск всех профилей, принадлежащих указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Список профилей {@link GeneratorProfileResponseDto} пользователя.
     */
    List<GeneratorProfileResponseDto> findByUserId(Long userId);

    /**
     * Возвращает список всех существующих профилей генерации.
     *
     * @return Список всех профилей {@link GeneratorProfileResponseDto}.
     */
    List<GeneratorProfileResponseDto> findAll();

    /**
     * Обновляет название профиля.
     *
     * @param id уникальный идентификатор профайла.
     * @param name новое название профиля.
     * @return Обновленный профиль {@link GeneratorProfileResponseDto}.
     */
    GeneratorProfileResponseDto updateName(Long id, String name);

    /**
     * Обновляет базовые настройки профиля генерации.
     *
     * @param id уникальный идентификатор профайла.
     * @param dto новые настройки {@link GeneratorProfileSettingsDto}.
     * @return Обновленный профиль {@link GeneratorProfileResponseDto}.
     */
    GeneratorProfileResponseDto updateSettings(Long id, GeneratorProfileSettingsDto dto);

    /**
     * Обновляет спецификацию алгоритма KDF (Key Derivation Function).
     *
     * @param id уникальный идентификатор профайла.
     * @param kdfAlgorithm новая спецификация алгоритма KDF {@link KdfAlgorithmSpec}.
     * @return Обновленный профиль {@link GeneratorProfileResponseDto}.
     */
    GeneratorProfileResponseDto updateKdfAlgorithm(Long id, KdfAlgorithmSpec kdfAlgorithm);

    /**
     * Обновляет спецификацию алгоритма шифрования.
     *
     * @param id уникальный идентификатор профайла.
     * @param cipherAlgorithm новая спецификация алгоритма шифрования {@link CipherAlgorithmSpec}.
     * @return Обновленный профиль {@link GeneratorProfileResponseDto}.
     */
    GeneratorProfileResponseDto updateCipher(Long id, CipherAlgorithmSpec cipherAlgorithm);

    /**
     * Удаляет все профили, принадлежащие указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteByUserId(Long userId);

    /**
     * Удаляет профили по точной дате создания.
     *
     * @param createAt дата создания.
     */
    void deleteByCreatedAt(LocalDateTime createAt);

    /**
     * Удаляет профили, созданные в заданном временном диапазоне.
     *
     * @param startDate начальная дата диапазона.
     * @param endDate   конечная дата диапазона.
     */
    void deleteByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Удаляет профили по их названию.
     *
     * @param name название удаляемых профилей.
     */
    void deleteByName(String name);

    /**
     * Удаляет профиль по уникальному идентификатору.
     *
     * @param id уникальный идентификатор профиля.
     */
    void deleteById(Long id);
}
