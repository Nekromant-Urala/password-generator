package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.matthew.NauJava.entity.KdfAlgorithm;

@RepositoryRestResource(path = "kdf")
public interface KdfAlgorithmRepository extends JpaRepository<KdfAlgorithm, Long> {
}
