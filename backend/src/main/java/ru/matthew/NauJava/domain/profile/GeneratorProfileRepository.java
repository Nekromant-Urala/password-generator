package ru.matthew.NauJava.domain.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "profiles")
public interface GeneratorProfileRepository extends JpaRepository<GeneratorProfile, Long> {
}
