package ru.matthew.NauJava.domain.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.domain.profile.dto.GeneratorProfileResponseDto;
import ru.matthew.NauJava.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;


public interface GeneratorProfileRepository extends JpaRepository<GeneratorProfile, Long> {

    /**
     * Поиск профилей по их названию.
     *
     * @param name название профиля.
     * @return Список профилей {@link GeneratorProfile} с совпадающим названием.
     */
    List<GeneratorProfile> findByName(String name);

    /**
     * Поиск профилей, созданных в заданном временном диапазоне.
     *
     * @param createAtAfter  начальная дата диапазона.
     * @param createAtBefore конечная дата диапазона.
     * @return Список профилей {@link GeneratorProfile}.
     */
    List<GeneratorProfile> findByCreateAtBetween(LocalDateTime createAtAfter, LocalDateTime createAtBefore);

    /**
     * Поиск профилей по точной дате создания.
     *
     * @param createAt дата создания.
     * @return Список профилей {@link GeneratorProfile}.
     */
    List<GeneratorProfile> findByCreateAt(LocalDateTime createAt);

    /**
     * Поиск всех профилей, принадлежащих указанному пользователю.
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Список профилей {@link GeneratorProfile} пользователя.
     */
    List<GeneratorProfile> findByUserId(Long userId);

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
    void deleteByCreateAt(LocalDateTime createAt);

    /**
     * Удаляет профили, созданные в заданном временном диапазоне.
     *
     * @param createAtAfter  начальная дата диапазона.
     * @param createAtBefore конечная дата диапазона.
     */
    void deleteByCreateAtBetween(LocalDateTime createAtAfter, LocalDateTime createAtBefore);

    /**
     * Удаляет профили по их названию.
     *
     * @param name название удаляемых профилей.
     */
    void deleteByName(String name);

    Long user(User user);
}
