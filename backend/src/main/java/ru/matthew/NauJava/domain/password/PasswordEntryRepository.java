package ru.matthew.NauJava.domain.password;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import ru.matthew.NauJava.domain.password.dto.PasswordEntryResponseDto;
import ru.matthew.NauJava.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "passwords")
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

    /**
     *
     * @param serviceName
     * @return
     */
    List<PasswordEntry> findByServiceName(String serviceName);

    /**
     *
     * @param userId
     * @return
     */
    List<PasswordEntry> findByUserId(Long userId);

    /**
     *
     * @param serviceName
     */
    void deleteByServiceName(String serviceName);
}
