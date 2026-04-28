package ru.matthew.NauJava.domain.algoritm.kdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "kdf")
public interface KdfAlgorithmRepository extends JpaRepository<KdfAlgorithm, Long> {
}
