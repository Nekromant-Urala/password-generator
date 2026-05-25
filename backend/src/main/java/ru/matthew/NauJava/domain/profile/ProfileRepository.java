package ru.matthew.NauJava.domain.profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Поиск профилей по их названию.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param name название профиля.
     * @return Список профилей {@link Profile} с совпадающим названием.
     */
    Optional<Profile> findByUserIdAndName(Long userId, String name);

    /**
     * Поиск профилей, созданных в заданном временном диапазоне.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param createAtAfter  начальная дата диапазона.
     * @param createAtBefore конечная дата диапазона.
     * @return Список профилей {@link Profile}.
     */
    Page<Profile> findAllByUserIdAndCreateAtBetween(Long userId, LocalDateTime createAtAfter, LocalDateTime createAtBefore, Pageable pageable);

    /**
     * Поиск профилей по точной дате создания.
     *
     * @param userId уникальный идентификатор пользователя.
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
     * Удаляет все профили, принадлежащие указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     */
    void deleteAllByUserId(Long userId);

    /**
     * Удаляет профили по точной дате создания.
     *
     * @param createAt дата создания.
     */
    void deleteAllByUserIdAndCreateAt(Long userId, LocalDateTime createAt);

    /**
     * Удаляет профили, созданные в заданном временном диапазоне.
     *
     * @param createAtAfter  начальная дата диапазона.
     * @param createAtBefore конечная дата диапазона.
     */
    void deleteAllByUserIdAndCreateAtBetween(Long userId, LocalDateTime createAtAfter, LocalDateTime createAtBefore);

    /**
     * Удаляет профили по их названию.
     *
     * @param userId уникальный идентификатор пользователя.
     * @param name название удаляемых профилей.
     */
    void deleteByUserIdAndName(Long userId, String name);
}
