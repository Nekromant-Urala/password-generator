package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.entity.ServiceEntry;

import java.util.Optional;

public interface ServiceEntryRepository extends JpaRepository<ServiceEntry, Long> {

    /**
     * Выполняет поиск записи сервиса
     *
     * @param name наименование сервиса
     * @return Optional с найденной записью сервиса, или пустой Optional, если запись не найдена
     */
    Optional<ServiceEntry> findServiceEntryByName(String name);
}
