package ru.matthew.NauJava.repository.criteria;

import ru.matthew.NauJava.entity.PasswordEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface EntryPasswordCriteriaApiRepository {

    /**
     * Выполняет поиск записей паролей, которые были сгенерированы за определенный промежуток времени
     *
     * @param startDate начальная дата временного промежутка для поиска
     * @param endDate   конечная дата временного промежутка для поиска
     * @return Возвращает список записей паролей в виде {@code List<PasswordEntry>}
     */
    List<PasswordEntry> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);


    /**
     * Выполняет поиск записей паролей, которые были созданы для какого-то конкретного сервиса
     *
     * @param serviceName наименование сервиса
     * @return Возвращает список записей паролей в виде {@code List<PasswordEntry>}
     */
    List<PasswordEntry> findByServiceName(String serviceName);
}
