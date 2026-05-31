package ru.matthew.NauJava.domain.profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Поиск профилей по их названию.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param name   название профиля.
     * @return Список профилей {@link Profile} с совпадающим названием.
     */
    Optional<Profile> findByUserIdAndName(Long userId, String name);

    /**
     * Поиск профилей, созданных в заданном временном диапазоне.
     *
     * @param userId         уникальный идентификатор пользователя.
     * @param createAtAfter  начальная дата диапазона.
     * @param createAtBefore конечная дата диапазона.
     * @return Список профилей {@link Profile}.
     */
    Page<Profile> findAllByUserIdAndCreateAtBetween(Long userId, LocalDateTime createAtAfter, LocalDateTime createAtBefore, Pageable pageable);

    /**
     * Поиск профилей по точной дате создания.
     *
     * @param userId   уникальный идентификатор пользователя.
     * @param createAt дата создания.
     * @return Список профилей {@link Profile}.
     */
    Page<Profile> findAllByUserIdAndCreateAt(Long userId, LocalDateTime createAt, Pageable pageable);

    /**
     * Поиск всех профилей, принадлежащих указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Список профилей {@link Profile} пользователя.
     */
    Page<Profile> findAllByUserId(Long userId, Pageable pageable);

    /**
     * Подсчитывает количество всех профайлов генерации конкретного пользователя
     *
     * @param userId уникальный идентификатор пользователя
     * @return количество всех профайлов генерации конкретного пользователя
     */
    @Query("SELECT count(p) FROM Profile p WHERE p.user.id = :userId")
    long countAllByUserId(@Param("userId") Long userId);

    /**
     * Находит самый популярный алгоритм, который применяют для шифрования
     *
     * @return название алгоритма
     */
    @Query("SELECT p.cipher FROM Profile p GROUP BY p.cipher ORDER BY count(p.cipher) DESC")
    List<CipherAlgorithmSpec> findMostPopularAlgorithm(Pageable pageable);


    /**
     * Ищет выбранный текущий профиль генерации конкретного пользователя
     *
     * @param userId уникальный идентификатор пользователя
     * @return выбранный профиль генерации для конкретного пользователя
     */
    @Query("SELECT p FROM Profile p WHERE p.user.id = :userId AND p.isFavorite = true")
    Optional<Profile> findByUserIdAndIsFavoriteTrue(@Param("userId") Long userId);

    /**
     * Сбрасывает выбранный профайл
     *
     * @param userId уникальный идентификатор пользователя
     */
    @Modifying
    @Query("UPDATE Profile p SET p.isFavorite = false WHERE p.user.id = :userId AND p.isFavorite = true")
    void resetFavoriteProfileForUser(@Param("userId") Long userId);

    /**
     * Удаляет все профили, принадлежащие указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    @Modifying
    @Query("DELETE Profile p WHERE p.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    /**
     * Удаляет профили по точной дате создания.
     *
     * @param createAt дата создания.
     */
    @Modifying
    void deleteAllByUserIdAndCreateAt(Long userId, LocalDateTime createAt);

    /**
     * Удаляет профили, созданные в заданном временном диапазоне.
     *
     * @param createAtAfter  начальная дата диапазона.
     * @param createAtBefore конечная дата диапазона.
     */
    @Modifying
    void deleteAllByUserIdAndCreateAtBetween(Long userId, LocalDateTime createAtAfter, LocalDateTime createAtBefore);

    /**
     * Удаляет профили по их названию.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param name   название удаляемых профилей.
     */
    @Modifying
    @Query("DELETE Profile p WHERE p.user.id = :userId AND p.name = :name")
    void deleteByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
}
