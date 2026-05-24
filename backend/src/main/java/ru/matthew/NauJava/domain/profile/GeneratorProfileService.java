package ru.matthew.NauJava.domain.profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileRequestDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileResponseDto;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для работы с профилями генерации.
 * Управляет настройками, алгоритмами формирования ключей (KDF) и шифрования.
 */
public interface GeneratorProfileService {

    /**
     * Создает новый профиль генерации
     *
     * @param userId уникальный идентификатор пользователя.
     * @param dto данные для создания профиля
     * @return Созданный профиль {@link GeneratorProfileResponseDto}
     */
    GeneratorProfileResponseDto createProfile(Long userId, GeneratorProfileRequestDto dto);

    /**
     * Создает новый профиль по умолчанию
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Созданный профиль {@link GeneratorProfileResponseDto}
     */
    GeneratorProfileResponseDto createDefaultProfile(Long userId);

    /**
     * Поиск профиля по уникальному идентификатору
     *
     * @param id уникальный идентификатор
     * @return Найденный профиль {@link GeneratorProfileResponseDto}
     */
    GeneratorProfileResponseDto findById(Long id);

    /**
     * Поиск профиля по его названию
     *
     * @param userId уникальный идентификатор пользователя.
     * @param name название профиля
     * @return Профиль {@link GeneratorProfileResponseDto} с совпадающим названием
     */
    GeneratorProfileResponseDto findByName(Long userId, String name);

    /**
     * Поиск профилей, созданных в заданном временном диапазоне
     *
     * @param userId уникальный идентификатор пользователя
     * @param startDate начальная дата диапазона
     * @param endDate   конечная дата диапазона
     * @return Список профилей {@link GeneratorProfileResponseDto}
     */
    Page<GeneratorProfileResponseDto> findAllByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Поиск профилей по точной дате создания.
     *
     * @param userId уникальный идентификатор пользователя
     * @param createAt дата создания профиля генерации
     * @return Список профилей {@link GeneratorProfileResponseDto}
     */
    Page<GeneratorProfileResponseDto> findAllByCreateAt(Long userId, LocalDateTime createAt, Pageable pageable);

    /**
     * Поиск всех профилей, принадлежащих указанному пользователю
     *
     * @param userId уникальный идентификатор пользователя
     * @return Список профилей {@link GeneratorProfileResponseDto} пользователя
     */
    Page<GeneratorProfileResponseDto> findAllByUserId(Long userId, Pageable pageable);

    /**
     * Возвращает список всех существующих профилей генерации
     *
     * @return Список всех профилей {@link GeneratorProfileResponseDto}
     */
    List<GeneratorProfileResponseDto> findAll();

    /**
     * Подсчитывает количество всех профайлов генерации конкретного пользователя
     *
     * @param userId уникальный идентификатор пользователя
     * @return количество всех профайлов генерации конкретного пользователя
     */
    long countAllProfilesByUserId(Long userId);

    /**
     * Обновляет базовые настройки профиля генерации
     *
     * @param id уникальный идентификатор профайла
     * @param dto новые настройки {@link GeneratorProfileUpdateDto}
     * @return Обновленный профиль {@link GeneratorProfileResponseDto}
     */
    GeneratorProfileResponseDto updateSettings(Long id, GeneratorProfileUpdateDto dto);

    /**
     * Удаляет все профили, принадлежащие указанному пользователю
     *
     * @param userId уникальный идентификатор пользователя
     */
    void deleteAllByUserId(Long userId);

    /**
     * Удаляет профили по точной дате создания
     *
     * @param userId уникальный идентификатор пользователя
     * @param createAt дата создания
     */
    void deleteAllByCreatedAt(Long userId, LocalDateTime createAt);

    /**
     * Удаляет профили, созданные в заданном временном диапазоне
     *
     * @param userId уникальный идентификатор пользователя
     * @param startDate начальная дата диапазона
     * @param endDate   конечная дата диапазона
     */
    void deleteAllByCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Удаляет профили по их названию.
     *
     * @param userId уникальный идентификатор пользователя
     * @param name название удаляемых профилей.
     */
    void deleteByName(Long userId, String name);

    /**
     * Удаляет профиль по уникальному идентификатору.
     *
     * @param userId уникальный идентификатор пользователя
     * @param id уникальный идентификатор профиля.
     */
    void deleteById(Long userId, Long id);
}
