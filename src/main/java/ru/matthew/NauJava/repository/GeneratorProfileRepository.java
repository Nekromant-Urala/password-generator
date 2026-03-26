package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matthew.NauJava.entity.GeneratorProfile;

import java.util.List;

@RepositoryRestResource(path = "profiles")
public interface GeneratorProfileRepository extends JpaRepository<GeneratorProfile, Long> {
}
