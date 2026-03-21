package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.matthew.NauJava.entity.PasswordEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

    /**
     * Выполняет поиск записей паролей, которые были сгенерированы за определенный промежуток времени
     *
     * @param startDate начальная дата временного промежутка для поиска
     * @param endDate   конечная дата временного промежутка для поиска
     * @return Возвращает список записей паролей в виде {@code List<PasswordEntry>}
     */
    List<PasswordEntry> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Удаление записей паролей по определенному названия сервиса
     *
     * @param serviceName наименование сервиса
     */
    void deleteByServiceName(String serviceName);


    /**
     * Выполняет поиск записей паролей, которые были созданы для какого-то конкретного сервиса
     *
     * @param serviceName наименование сервиса
     * @return Возвращает список записей паролей в виде {@code List<PasswordEntry>}
     */
    @Query("SELECT e FROM PasswordEntry e WHERE e.serviceName = :serviceName")
    List<PasswordEntry> findByServiceName(@Param("serviceName") String serviceName);
}
