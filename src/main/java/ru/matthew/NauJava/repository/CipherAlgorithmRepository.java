package ru.matthew.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matthew.NauJava.entity.CipherAlgorithm;

public interface CipherAlgorithmRepository extends JpaRepository<CipherAlgorithm, Long> {
}
