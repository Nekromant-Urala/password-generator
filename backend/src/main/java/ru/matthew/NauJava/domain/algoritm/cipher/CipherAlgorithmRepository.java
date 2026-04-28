package ru.matthew.NauJava.domain.algoritm.cipher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "cipher")
public interface CipherAlgorithmRepository extends JpaRepository<CipherAlgorithm, Long> {
}
